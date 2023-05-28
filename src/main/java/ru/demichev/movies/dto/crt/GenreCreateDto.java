package ru.demichev.movies.dto.crt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreCreateDto {
    @NotBlank(message = "Genre name can't be null")
    @Size(max=15, message = "Name is too long")
    private String name;
}
