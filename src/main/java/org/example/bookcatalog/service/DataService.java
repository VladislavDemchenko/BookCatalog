package org.example.bookcatalog.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class DataService extends SessionService {

    public <T> ResponseEntity<?> crate(T entity, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(bindingResult.getFieldError());
        }
        executeInTransaction((em) -> em.persist(entity));
        return ResponseEntity.ok("Validation successful");
    }

    public <T> ResponseEntity<?> delete(Long id, Class<T> entityType){
        validId(id, entityType.getSimpleName());
        executeInTransaction((em) -> em.remove(em.find(entityType, id)));
        return ResponseEntity.ok("Validation successful");
    }


    public <T> ResponseEntity<?> findById(Long id, Class<T> entityType) {
        validId(id, entityType.getSimpleName());
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

    public <T>ResponseEntity<?> findByName(String name, Class<T> entityType){

        var catalog =  executeInTransactionReturning((em) -> {
            return em.createQuery("select c from "+entityType.getSimpleName()+" c where c.name = :name")
                    .setParameter("name", name)
                    .getSingleResult();
        });
        if(catalog == null){
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(catalog);
        }
    }

    public <T> ResponseEntity<?> changeName(Long id, String newName, Class<T> entityType){
        validId(id, entityType);
        if(newName.isEmpty()){
            throw new InvalidRequestException("name of catalog can`t be empty");
        }
        if(Objects.equals(executeInTransactionReturning((em) -> em.find(entityType, id)).getClass().getName(), newName)){
            throw new InvalidRequestException("this name catalog is already exist");
        }

        executeInTransaction((em) -> { // check for setter into an object and rename field
            T currentEntity = em.find(entityType, id);
            if (currentEntity == null) {
                throw new EntityNotFoundException("Object with id: " + id + " not found");
            }

            try {
                Method setNameMethod = entityType.getMethod("setName", String.class);
                setNameMethod.invoke(currentEntity, newName);

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new UnsupportedOperationException("Renaming is not supported for an object of type " + entityType.getSimpleName());
            }

            try {
                Method setCreationDateMethod = entityType.getMethod("setCreationDate", LocalDateTime.class);
                setCreationDateMethod.invoke(currentEntity, LocalDateTime.now());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new UnsupportedOperationException("Updating the creation date is not supported for an object of type " + entityType.getSimpleName());
            }
            em.merge(currentEntity);
        });

        return ResponseEntity.ok("Validation successful");
    }
    
    public ResponseEntity<?> changeDescriptionName(Long id, String descriptionName){ //Catalog method
        validId(id, Catalog.class);
        if(Objects.equals(executeInTransactionReturning((em) -> em.find(Catalog.class, id)).getDescription(), descriptionName)){
            throw new InvalidRequestException("this description is already exist");
        }
        executeInTransaction((em)-> em.find(Catalog.class, id).setDescription(descriptionName));
        return ResponseEntity.ok("Validation successful");
    }




    public DataService(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }


    public <T> ResponseEntity<?> sortByName(Class<T> entityType){
        if(entityType == null){
            throw new EntityNotFoundException("not found entity");
        }

        Field[] fields = entityType.getDeclaredFields();
        boolean hasNameField = false;

        // Перевірка кожного поля на співпадіння з ім'ям "name"
        for (Field field : fields) {
            if (field.getName().equals("name")) {
                hasNameField = true;
                break;
            }
        }

        if (!hasNameField) {
            throw new IllegalArgumentException("Class " + entityType.getSimpleName() + " not found field name");
        }

        executeInTransaction((em) ->{
            em.createQuery("select e from " + entityType.getSimpleName() + " e order by e.name");
        });
        return ResponseEntity.ok("Validation successful");
    }


    public <T> ResponseEntity<?> sortByLastUpdate(Class<T> entityType){
        if(entityType == null){
            throw new EntityNotFoundException("not found entity");
        }

        List<T> object = executeInTransactionReturning((em) -> em.createQuery("SELECT e FROM " + entityType.getSimpleName() + " e ORDER BY e.creationDate DESC", entityType).getResultList());

        if (object.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok("Validation successful");
        }
    }
}
