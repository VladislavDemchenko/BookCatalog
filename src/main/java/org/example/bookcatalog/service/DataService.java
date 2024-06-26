package org.example.bookcatalog.service;

import jakarta.persistence.*;
import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.exception.UnsupportedContractException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataService extends DataAccessService{

    public <T,B> ResponseEntity<?> create(T entity, FieldDto<B> fieldDto, BindingResult bindingResult){
        validateEntity(entity); // not use when we have annotation @Valid under entity
        checkingValueUnique(entity, fieldDto);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError());
        }

        validateAndUpdateCreationDate(entity);
        executeInTransaction((em) -> em.persist(entity));

        return ResponseEntity.ok("Validation successful");

    }

    public <T> ResponseEntity<?> delete(Long id, Class<T> entityType){
        validId(id, entityType);
        executeInTransaction(em -> {
            T entity = em.find(entityType, id);
            em.refresh(entity);
            em.remove(entity);
        });
        return ResponseEntity.ok("Validation successful");
    }

    public <T> ResponseEntity<?> findById(Long id, Class<T> entityType) {
        validId(id, entityType);
        return ResponseEntity.ok(executeInTransactionReturning((em) -> em.find(entityType, id)));
    }

    
    public <T> ResponseEntity<?> findAll(Class<T> entityType) {
        List<T> objects = executeInTransactionReturning((em) -> em.createQuery("select c from " + entityType.getSimpleName() + " c", entityType).getResultList());

        if (objects.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(objects);
        }
    }

    public <T> ResponseEntity<?> findByName(String name, Class<T> entityType){
        var entity = executeInTransactionReturning((em) -> {
            try {
                System.out.println(name);
                return em.createQuery("select c from " + entityType.getSimpleName() + " c where c.name = :name")
                        .setParameter("name", name)
                        .getSingleResult();
            }catch (NoResultException e){
                System.out.println(e);
                em.getTransaction().commit();
                throw new InvalidRequestException("Not found current name");
            }
        });
        return ResponseEntity.ok(entity);
    }



    public <T,B> ResponseEntity<?> updateName(Long id, String newName, FieldDto<B> fieldDto, Class<T> entityType){
        validId(id, entityType);
        if(newName.isEmpty()){
            throw new InvalidRequestException("New name of field (" + fieldDto.getFieldName() + ") can`t be empty");
        }

        checkingValueUnique(executeInTransactionReturning(em -> em.find(entityType, id)), fieldDto);

        executeInTransaction((em) -> { // check for setter into an object and rename field
            T currentEntity = em.find(entityType, id);

            try {
                Method setNameMethod = entityType.getMethod("setName", String.class);
                setNameMethod.invoke(currentEntity, newName);

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new UnsupportedOperationException("Renaming is not supported for an object of type " + entityType.getSimpleName());
            }
            validateAndUpdateCreationDate(currentEntity);
            em.merge(currentEntity);
        });

        return ResponseEntity.ok("Validation successful");
    }
    
    


    

    public <T> ResponseEntity<?> sortByName(Class<T> entityType){
        if(!checkingSupportFieldContract(entityType, new FieldDto<String>("name"))){
            throw new UnsupportedContractException("Unsupported name field contract exception in " + entityType + " class");
        }

        executeInTransaction((em) ->{
            em.createQuery("select e from " + entityType.getSimpleName() + " e order by e.name");
        });
        return ResponseEntity.ok("Validation successful");
    }

    
    
    public <T> ResponseEntity<?> sortByLastUpdate(Class<T> entityType){
        validateEntity(entityType);

        if(!checkingSupportFieldContract(entityType, new FieldDto<LocalDateTime>("creationDate"))){
            throw new UnsupportedContractException("Unsupported creationDate field contract exception in " + entityType + " class");
        }

        List<T> object = executeInTransactionReturning((em) -> em.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e ORDER BY e.creationDate DESC", entityType).getResultList());

        if (object.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok("Validation successful");
        }
    }
    public DataService(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }


}
