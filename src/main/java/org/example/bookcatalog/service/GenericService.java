package org.example.bookcatalog.service;

import org.example.bookcatalog.entity.Catalog;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface GenericService <T, B>{
    T crate(B entity, BindingResult bindingResult);

    T delete(Long id);

    T findById(Long id);

    T findAll();
}
