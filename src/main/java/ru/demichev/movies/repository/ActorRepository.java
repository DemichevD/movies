package ru.demichev.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.dto.ActorDto;

import java.util.List;
import java.util.Optional;
@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Query(value = "from Actor a where a.id not in ( select a.id from Actor a left join a.movies m where m.id = :movie_id)")
    List<Actor> findActorNotAssignedToMovie(@Param("movie_id") long movieId);

    @Query(value = "from Actor a where a.id in (select a.id from Actor a left join a.movies m where m.id = :movie_id)")
    List<Actor> findActorAssignedToMovie(@Param("movie_id") long movieId);

    @Query(value="select new ru.demichev.movies.dto.ActorDto(a.id, a.firstName, a.secondName, a.lastName, a.yearOfBirth, a.biography, a.country) from " +
        "Actor a where a.id in (select a.id from Actor a left join a.movies m where m.id = :movie_id)"
        )
    List<ActorDto> findActorAssignedToMovieWithProjection(@Param("movie_id") long movieId);

    Boolean existsByFirstNameAndSecondNameAndLastNameAndYearOfBirth(String firstName, String secondName, String LastName, int year);

    Optional<Actor> findActorByFirstNameAndSecondNameAndLastNameAndYearOfBirth(String firstName, String secondName, String LastName, int year);
}
