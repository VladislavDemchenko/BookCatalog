package org.example.bookcatalog.service;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.example.bookcatalog.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
@Transactional
public class EntityManagerService {
    private final EntityManager em;
    public String addUser(Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
        }
        executeInTransaction(em -> em.persist(book));
        return "all right";
    }



    public String findById(Long id) {
        String x = exceptionHandler(id);
        if (x != null) return x;
        return executeInTransactionReturning(em -> em.find(Book.class, id)).toString();
    }

//    public String changing(UpdateUserRequest updateUserRequest, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()){
//            return Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
//        }
//        return executeInTransactionReturning(em -> {
//            var newUser = em.find(Person.class, updateUserRequest.id());
//            newUser.setFirstName(updateUserRequest.user().getFirstName());
//            newUser.setLastName(updateUserRequest.user().getLastName());
//            newUser.setEmail(updateUserRequest.user().getEmail());
//            return newUser.toString();
//        });
//
//    }
    public String delete(Long id) {
        String x = exceptionHandler(id);
        if (x != null) return x;
        executeInTransaction(entityManager -> {
            entityManager.remove(entityManager.find(Book.class, id));
        });
        return null;
    }

    public List<Book> getAllUsers() {
        return executeInTransactionReturning(entityManager -> {
            return entityManager.createQuery("select b from Book b", Book.class)
                    .getResultList();
        });
    }

    private String exceptionHandler(Long id) {
        Long countOfId= executeInTransactionReturning(
                entityManager -> entityManager.createQuery("SELECT COUNT(u.id) FROM Person u WHERE u.id = :userId", Long.class)
                        .setParameter("userId", id)
                        .getSingleResult());
        System.out.println(countOfId);
        if(id == null || countOfId != 1){
            return "this User is not valid";
        }
        return null;
    }

    public void executeInTransaction(Consumer<EntityManager> entityManagerConsumer){ //work in session with void return type
        executeInTransactionReturning(entityManager -> {
            entityManagerConsumer.accept(em);
            return null;
        });
    }

    public <T>T executeInTransactionReturning(Function<EntityManager, T> entityManagerFunction){ //work in session with T return type
        try {
            em.getTransaction().begin();
            var result = entityManagerFunction.apply(em);
            em.getTransaction().commit();
            return result;
        }catch (RuntimeException e){
            em.getTransaction().rollback();
            throw e;
        }
    }
    @Autowired
    public EntityManagerService(EntityManagerFactory em) {
        this.em = em.createEntityManager();
    }

    @PreDestroy
    public void preDestroy(){
        em.close();
    }
}
