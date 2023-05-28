package ru.demichev.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    private Long id;
    private String title;
    private Integer year;
    private String description;
    private String director;
    private Set<String> actors;
    private Set<String> genres;
    public MovieDto(Long id, String title){
        this.id = id;
        this.title = title;
    }
}
