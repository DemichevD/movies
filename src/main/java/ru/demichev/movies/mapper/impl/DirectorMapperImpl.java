package ru.demichev.movies.mapper.impl;

import org.springframework.stereotype.Component;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.dto.crt.DirectorCreateDto;
import ru.demichev.movies.dto.DirectorDto;
import ru.demichev.movies.mapper.DirectorMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class DirectorMapperImpl implements DirectorMapper {
    @Override
    public DirectorDto toDto(Director source) {
        if(source == null){
            return null;
        }
        DirectorDto dto = new DirectorDto();
        dto.setId(source.getId());
        dto.setFirstName(source.getFirstName());
        dto.setSecondName(source.getSecondName());
        dto.setLastName(source.getLastName());
        dto.setYearOfBirth(source.getYearOfBirth());
        dto.setBiography(source.getBiography());
        dto.setCountry(source.getCountry());
        dto.setMovies(source.getMovies() != null ? source.getMovies().stream().map(Movie::getTitle).collect(Collectors.toList()) : new ArrayList<>());
        return dto;
    }

    @Override
    public Director toEntity(DirectorCreateDto source) {
        if ( source == null ) {
            return null;
        }

        Director director = new Director();

        director.setFirstName(source.getFirstName());
        director.setSecondName( source.getSecondName() );
        director.setLastName( source.getLastName() );
        director.setYearOfBirth( source.getYearOfBirth() );
        director.setBiography( source.getBiography() );
        director.setCountry( source.getCountry() );

        return director;
    }
}
