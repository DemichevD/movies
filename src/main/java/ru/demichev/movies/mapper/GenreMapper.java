package ru.demichev.movies.mapper;

import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.dto.crt.GenreCreateDto;
import ru.demichev.movies.dto.GenreDto;

//@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDto toDto(Genre source);
    Genre toEntity(GenreCreateDto source);
}
