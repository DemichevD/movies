package ru.demichev.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActorDto {
    private Long id;
    private String firstName;
    private String secondName;
    private String lastName;
    private Integer yearOfBirth;
    private String biography;
    private String country;
    private Set<String> movies;

    public ActorDto(Long id, String firstName, String secondName, String lastName, Integer yearOfBirth, String biography, String country){
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.biography = biography;
        this.country = country;
    }


}
