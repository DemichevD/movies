package ru.demichev.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.demichev.movies.domain.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long>, CustomGenreRepository {
    Optional<Genre> findByNameIgnoreCase(String genreName);
    List<Genre> findAllByNameContainingIgnoreCase(String genreName);
    boolean existsByNameIgnoreCase(String genreName);
    List<Genre> findAllByOrderByName();
}
