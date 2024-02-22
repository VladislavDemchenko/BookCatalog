package org.example.bookcatalog.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class MainService {

    private EntityManager entityManager;

    public void executeInTransaction(Consumer<EntityManager> entityManagerConsumer){ //work in session with void return type
        executeInTransactionReturning(entityManager -> {
            entityManagerConsumer.accept(entityManager);
            return null;
        });
    }

    public <T>T executeInTransactionReturning(Function<EntityManager, T> entityManagerFunction){ //work in session with T return type
        try {
            entityManager.getTransaction().begin();
            var result = entityManagerFunction.apply(entityManager);
            entityManager.getTransaction().commit();
            return result;
        }catch (RuntimeException e){
            entityManager.getTransaction().rollback();
            throw e;
        }
    }

    public String exceptionHandler(Long id) {
        Long countOfId= executeInTransactionReturning(
                entityManager -> entityManager.createQuery("SELECT COUNT(u.id) FROM Catalog u WHERE u.id = :userId", Long.class)
                        .setParameter("userId", id)
                        .getSingleResult());
        System.out.println(countOfId);
        if(id == null || countOfId != 1){
            return "this Catalog is not valid";
        }
        return null;

    }

    @Autowired
    public MainService(EntityManagerFactory entityManagerFactory) {
        entityManager = entityManagerFactory.createEntityManager();
    }
}
