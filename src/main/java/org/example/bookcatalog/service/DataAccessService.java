package org.example.bookcatalog.service;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
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



    public <T> void validId(Long id, Class<T> entityClass) {// method provides greater flexibility and control over validation, but you can also use BindingResult
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
            throw new InvalidRequestException("Not found value id. Please provide a valid identifier.");
        }
    }

    public <T> void validateEntity(T entity) {
        if(entity == null){
            throw new EntityNotFoundException("not found entity");
        }
    }

    public <T, B> void checkingUniqueValue(T entity, FieldDto<B> fieldDto){ ///////подумати альтернативу, бо такий спосіб череват купою помилок
        checkingSupportFieldContract(entity.getClass(), fieldDto);
        boolean isUnique = false;
        try {
            Object method = entity.getClass().getMethod("get" + fieldDto.getFieldName()).invoke(entity);
            Field field = entity.getClass().getDeclaredField(fieldDto.getFieldName());
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null){
                 isUnique = annotation.unique();
            }else {
//                executeInTransactionReturning(em -> em.createQuery("SELECT COUNT(u." + fieldDto.getFieldName() + ") FROM " + entity.getClass().getSimpleName() + " u WHERE u." + fieldDto.getFieldName() + " = :name")
//                        .setParameter(fieldDto.getFieldName(), method.invoke(entity)));
            }
        }catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e){
            e.getStackTrace();
        }
    }



    public <T> void checkingSupportMethodContract(Class<T> entityType){

    }




    public <T, B> boolean checkingSupportFieldContract(Class<T> entityType, FieldDto<B> fieldDto) { // checking to support the entity contract by valueType of field
        Field[] fields = entityType.getDeclaredFields();
        boolean hasFieldName = false;
        Class<?> hasFieldType = null;

        for (Field field : fields) {
            if (field.getName().equals(fieldDto.getFieldName())) {
                hasFieldName = true;
                hasFieldType = field.getType();
                break;
            }
        }
//        if(!hasFieldName){
//            throw new UnsupportedEntityFieldException("Class " + entityType.getSimpleName() + " not found field " + fieldName);
//        }
//        if(hasFieldType != valueType){
//            throw new UnsupportedEntityFieldException("Type of " + fieldName + " field is not supported");
//        }
        return hasFieldName && hasFieldType == fieldDto.getFieldType();

    }

//    public <T> void updateCreationDate(T entity){
//        try {
//            Method setNameMethod = entity.getClass().getMethod("setCreationDate", LocalDateTime.class);
//            setNameMethod.invoke(entity, LocalDateTime.now());
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
//            throw new UnsupportedOperationException("Cannot update current time in entity " + entity.getClass().getSimpleName());
//        }
//    }

    public <T> void updateCreationDate(T entity) {
        try {
            Method getCreationDateMethod = entity.getClass().getMethod("getCreationDate");
            Method setCreationDateMethod = entity.getClass().getMethod("setCreationDate", LocalDateTime.class);

            LocalDateTime currentDateTime = (LocalDateTime) getCreationDateMethod.invoke(entity);
            if (currentDateTime == null) {
                setCreationDateMethod.invoke(entity, LocalDateTime.now());
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException("Cannot update creation date in entity " + entity.getClass().getSimpleName());
        }
    }



    @Autowired
    public DataAccessService(EntityManagerFactory entityManagerFactory) {
        entityManager = entityManagerFactory.createEntityManager();
    }
}
