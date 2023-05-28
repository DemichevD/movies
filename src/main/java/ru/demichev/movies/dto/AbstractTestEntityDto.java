package ru.demichev.movies.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AbstractTestEntityDto {
    private Long id;
    private String name;
    private String context;
}
