package org.example.bookcatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDto<T>{
    private String fieldName;
    private Class<T> fieldType;

    public FieldDto(String fieldName) {
        this.fieldName = fieldName;
    }
}
