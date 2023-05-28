package ru.demichev.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Movie;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query(value = "from Movie m where m.id in (select m.id from Movie m inner join m.actors a where a.id = :actor_id)")
    List<Movie> findMoviesByActorId(@Param("actor_id") long actorId);

    @Query(value="from Movie m join fetch m.director")
    List<Movie> findAllMovieWithDirector();

    @Transactional
    Optional<Movie> findMovieByTitleAndYear(String movieTitle, Integer year);

    Boolean existsByTitleAndYear(String title,  Integer year);

    @Transactional
    Optional<Movie> findByTitleAndYear(String title, Integer year);
}
