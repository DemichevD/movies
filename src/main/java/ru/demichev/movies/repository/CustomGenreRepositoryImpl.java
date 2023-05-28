package ru.demichev.movies.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.dto.GenreDto;
import ru.demichev.movies.dto.MovieDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.*;

@Slf4j
@Repository
@Transactional(readOnly = true)
public class CustomGenreRepositoryImpl implements CustomGenreRepository{

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<GenreDto> findGenreWithMovie(Collection<Long> genreIds, int topMoviesCount) {
        log.info("genreId - {}", genreIds);
        List<Tuple> tuples = entityManager.createNativeQuery(
                        """
                         select * from (
                         (select g.id as genre_id, 
                         g.name as genre_name, 
                         m.id as movie_id, 
                         m.name as movie_name, 
                         m.rating as movie_rating, 
                         row_number() over (
                             partition by genre_id 
                             order by movie_rating desc
                         ) as rn 
                         from Genre g 
                         where g.id in (:genreIds) 
                         left join g.movies m where m.id = g.id) sub 
                         where sub.rn <= :topMoviesCount""",
                        Tuple.class
                ).setParameter("genreIds", genreIds)
                .setParameter("topMoviesCount", topMoviesCount)
                .getResultList();

        Map<Long, GenreDto> genres = new HashMap<>();
        for (Tuple tuple : tuples){
            Long genreId = tuple.get("genre_id", Long.class);
            GenreDto genreDto = genres.computeIfAbsent(
                    genreId,
                    k-> new GenreDto(genreId, tuple.get("genre_name", String.class), new HashSet<>())
            );
            Long movieId = tuple.get("movie_id", Long.class);
            if (movieId != null){
                genreDto.getMovies().add(new MovieDto(movieId, tuple.get("movie_title", String.class)));
            }
        }

        return new ArrayList<>(genres.values());
    }
}
