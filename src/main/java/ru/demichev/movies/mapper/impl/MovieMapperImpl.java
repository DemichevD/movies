package ru.demichev.movies.mapper.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.dto.MovieDto;
import ru.demichev.movies.dto.crt.MovieCreateDto;
import ru.demichev.movies.mapper.MovieMapper;
import ru.demichev.movies.repository.DirectorRepository;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class MovieMapperImpl implements MovieMapper {
    private final DirectorRepository directorRepository;

    public MovieMapperImpl(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Override
    public MovieDto toDto(Movie source) {
        if(source == null){
            return null;
        }

        MovieDto movieDto = new MovieDto();
        movieDto.setId(source.getId());
        movieDto.setTitle(source.getTitle());
        movieDto.setDescription(source.getDescription());
        movieDto.setYear(source.getYear());
        movieDto.setDirector(source.getDirector() != null ? source.getDirector().getLastName() : "");
        movieDto.setActors(source.getActors() != null ? source.getActors().stream().map(Actor -> Actor.getFirstName() + " " + Actor.getLastName()).collect(Collectors.toSet()) : new HashSet<>());
        movieDto.setGenres(source.getGenres() != null ? source.getGenres().stream().map(Genre::getName).collect(Collectors.toSet()) : new HashSet<>());
        return movieDto;
    }

    @Override
    public Movie toEntity(MovieCreateDto source) {
        if(source == null){
            return null;
        }

        return new Movie(
                source.getTitle(),
                source.getDescription(),
                source.getYear(),
                directorRepository.getReferenceById(source.getDirectorId())
        );
    }
}
