package ru.demichev.movies.mapper;


import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.dto.crt.MovieCreateDto;
import ru.demichev.movies.dto.MovieDto;

//@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieDto toDto(Movie source);

    Movie toEntity(MovieCreateDto source);
}
