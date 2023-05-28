package ru.demichev.movies.mapper.impl;

import org.springframework.stereotype.Component;
import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.dto.crt.GenreCreateDto;
import ru.demichev.movies.dto.GenreDto;
import ru.demichev.movies.dto.MovieDto;
import ru.demichev.movies.mapper.GenreMapper;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class GenreMapperImpl implements GenreMapper {
    @Override
    public GenreDto toDto(Genre source) {
        if(source == null){
            return null;
        }
        GenreDto genreDto = new GenreDto();
        genreDto.setId(source.getId());
        genreDto.setName(source.getName());
        genreDto.setMovies(source.getMovies() != null ? source.getMovies().stream().map(movie-> new MovieDto(movie.getId(), movie.getTitle())).collect(Collectors.toSet()) : new HashSet<>());
        return genreDto;
    }

    @Override
    public Genre toEntity(GenreCreateDto source) {
        if(source == null){
            return null;
        }
        Genre genre = new Genre();
        genre.setName(source.getName());
        return genre;
    }
}
