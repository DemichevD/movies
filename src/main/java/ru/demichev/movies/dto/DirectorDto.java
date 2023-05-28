package ru.demichev.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorDto {
    private Long id;
    private String firstName;
    private String secondName;
    private String lastName;
    private Integer yearOfBirth;
    private String biography;
    private String country;
    private List<String> movies;
}
