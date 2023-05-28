package ru.demichev.movies.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestValidationErrorDto {
    private List<FieldValidationErrorDto> errors;
}
