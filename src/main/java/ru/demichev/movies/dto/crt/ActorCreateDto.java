package ru.demichev.movies.dto.crt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorCreateDto {
    @NotBlank(message = "Actor first Name can't be null")
    private String firstName;
    private String secondName;
    @NotBlank(message = "Actor last Name can't be null")
    private String lastName;
    @NotNull(message = "Actor year of birth can't be null")
    private Integer yearOfBirth;
    private String biography;
    private String country;
}
