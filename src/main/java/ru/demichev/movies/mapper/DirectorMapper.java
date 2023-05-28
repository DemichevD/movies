package ru.demichev.movies.mapper;

import ru.demichev.movies.domain.Director;
import ru.demichev.movies.dto.crt.DirectorCreateDto;
import ru.demichev.movies.dto.DirectorDto;

//@Mapper(componentModel = "spring")
public interface DirectorMapper {

    DirectorDto toDto(Director source);

    Director toEntity(DirectorCreateDto source);
}
