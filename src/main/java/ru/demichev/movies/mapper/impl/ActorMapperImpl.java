package ru.demichev.movies.mapper.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.dto.crt.ActorCreateDto;
import ru.demichev.movies.dto.ActorDto;
import ru.demichev.movies.mapper.ActorMapper;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@Transactional
public class ActorMapperImpl implements ActorMapper {
    @Override
    public ActorDto toDto(Actor source) {
        ActorDto dto = new ActorDto();
        dto.setId(source.getId());
        dto.setFirstName(source.getFirstName());
        dto.setSecondName(source.getSecondName());
        dto.setLastName(source.getLastName());
        dto.setYearOfBirth(source.getYearOfBirth());
        dto.setBiography(source.getBiography());
        dto.setCountry(source.getCountry());
        dto.setMovies(source.getMovies() != null ? source.getMovies().stream().map(Movie::getTitle).collect(Collectors.toSet()) : new HashSet<>());
        return dto;
    }

    @Override
    public Actor toEntity(ActorCreateDto source) {
        if ( source == null ) {
            return null;
        }

        Actor actor = new Actor();

        actor.setFirstName( source.getFirstName());
        actor.setSecondName( source.getSecondName() );
        actor.setLastName( source.getLastName() );
        actor.setYearOfBirth( source.getYearOfBirth() );
        actor.setBiography( source.getBiography() );
        actor.setCountry( source.getCountry() );

        return actor;
    }
}
