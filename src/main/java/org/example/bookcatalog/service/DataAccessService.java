package org.example.bookcatalog.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.exception.UnsupportedEntityFieldException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class DataAccessService {

    private final EntityManager entityManager;

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



    public  <T> void validId(Long id, T entity) {// method provides greater flexibility and control over validation, but you can also use BindingResult
        Class<?> entityClass = entity.getClass();

        Long countOfId = executeInTransactionReturning(
                entityManager -> entityManager.createQuery(
                                "SELECT COUNT(u.id) FROM " + entityClass.getSimpleName() + " u WHERE u.id = :userId",
                                Long.class)
                        .setParameter("userId", id)
                        .getSingleResult()
        );

        if (id == null) {
            throw new InvalidRequestException("Operation cannot be performed: identifier is missing. Please check the entered data and try again.");
        } else if (countOfId != 1) {
            throw new InvalidRequestException("Invalid identifier: the value of id cannot be zero. Please provide a valid identifier.");
        }
    }

    public <T> void validateEntity(Class<T> entityType) {
        if(entityType == null){
            throw new EntityNotFoundException("not found entity");
        }
    }

    public <T> void checkingSupportFieldContract(Class<T> entityType, String fieldName) { // checking to support the entity contract by name of field
        Field[] fields = entityType.getDeclaredFields();
        boolean hasFieldName = false;

        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                hasFieldName = true;
                break;
            }
        }
        if(!hasFieldName){
            throw new UnsupportedEntityFieldException("Class " + entityType.getSimpleName() + " not found field " + fieldName);
        }
    }

    public <T, B> void checkingSupportFieldTypeContract(Class<T> entityType, String fieldName, Class<B> type) { // checking to support the entity contract by type of field
        Field[] fields = entityType.getDeclaredFields();
        Class hasFieldType = null;

        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                hasFieldType = field.getType();
                break;
            }
        }
        if(hasFieldType != type){
            throw new UnsupportedEntityFieldException("Type of " + fieldName + " field is not supported");
        }
    }

    @Autowired
    public DataAccessService(EntityManagerFactory entityManagerFactory) {
        entityManager = entityManagerFactory.createEntityManager();
    }
}
