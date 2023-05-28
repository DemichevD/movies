package ru.demichev.movies.mapper;

import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.dto.crt.ActorCreateDto;
import ru.demichev.movies.dto.ActorDto;

//@Mapper(componentModel = "spring")
public interface ActorMapper {

    ActorDto toDto(Actor source);

    Actor toEntity(ActorCreateDto source);
}
