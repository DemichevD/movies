package ru.demichev.movies.repository;

import ru.demichev.movies.dto.GenreDto;

import java.util.Collection;
import java.util.List;

public interface CustomGenreRepository {
    List<GenreDto> findGenreWithMovie(Collection<Long> genreIds, int topMoviesCount);
}
