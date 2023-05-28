package ru.demichev.movies.dto.crt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectorCreateDto {

    @NotBlank(message = "Director first Name can't be null")
    private String firstName;
    private String secondName;
    @NotBlank(message = "Director last Name can't be null")
    private String lastName;
    @NotNull(message = "Year of birth can't be null")
    private Integer yearOfBirth;
    private String biography;
    private String country;
}
