package ru.demichev.movies.dto.crt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieCreateDto {

    @NotBlank(message = "Movie Title can't be null")
    private String title;
    @Min(value=1980, message = "Only new movies")
    @NotNull(message = "Movie year can't be null")
    private Integer year;
    private String description;
    private Long directorId;

}
