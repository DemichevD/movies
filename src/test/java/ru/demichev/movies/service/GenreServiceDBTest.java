package ru.demichev.movies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.service.configuration.GenreServiceConfiguration;
import ru.demichev.movies.testUtil.DBTest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DBTest
@ContextConfiguration(classes = GenreServiceConfiguration.class)
public class GenreServiceDBTest extends IntegrationSuite {
    @Autowired
    private GenreService genreService;
    @Autowired
    private TestDBFacade db;

    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
    }

    @Test
    void shouldFindByNameSuccessfully(){
        Genre genre = genreService.createGenre(new Genre("Боевик"));
        Genre genre2 = genreService.createGenre(new Genre("Мультфильм"));
        Genre result = genreService.findByName("боевик");
        assertNotNull(result.getId());
        assertEquals(genre.getName(), result.getName());
        assertNotEquals(genre2.getName(), result.getName());
        assertEquals(2,db.count(Genre.class));
    }

    @Test
    void shouldRollbackFindByName(){
        db.persist(new Genre("Боевик"));
        assertThrows(
                NotFoundException.class,
                ()->genreService.findByName("Мультфильм")
        );
        assertEquals(1, db.count(Genre.class));
    }

    @Test
    void shouldCreateSuccessfully(){
        Genre genre = genreService.createGenre(new Genre("Боевик"));
        assertNotNull(genre.getId());
        assertEquals("Боевик", genre.getName());
        assertEquals(1,db.count(Genre.class));

    }

    @Test
    void shouldRollbackCreateGenre(){
        db.persist(new Genre("Боевик"));
        assertThrows(
                ExistsException.class,
                ()->genreService.createGenre(new Genre("Боевик"))
        );
        assertEquals(1, db.count(Genre.class));
    }


    @Test
    void shouldRollbackCreateManyGenres(){
        db.persist(new Genre("Фантастика"));

        assertThrows(
                ExistsException.class,
                ()->genreService.createGenres(
                        List.of(
                                new Genre("Боевик"),
                                new Genre("Биография"),
                                new Genre("Мультфильм"),
                                new Genre("Фантастика"),
                                new Genre("Приключения")
                        )
                )
        );

        assertEquals(1,db.count(Genre.class));
    }


    @Test
    void shouldFindByContainingPrefixSuccessfully(){
        genreService.createGenres(
                List.of(
                        new Genre("Боевик"),
                        new Genre("Биография"),
                        new Genre("Мультфильм"),
                        new Genre("Фантастика"),
                        new Genre("Приключения")
                )
        );

        List<Genre> resultList = genreService.findByContainingPrefix("б");
        assertEquals(2, resultList.size());
        assertEquals(resultList.get(0).getName(),"Боевик");
        assertEquals(resultList.get(1).getName(),"Биография");
    }

    @Test
    void shouldUpdateGenreSuccessfully(){
        Genre genre = genreService.createGenre(new Genre("боевик"));
        assertNotNull(genre.getId());
        Genre updatedGenre = genreService.updateGenre(new Genre("Боевик"), genre.getId());
        assertEquals("Боевик", updatedGenre.getName());
        assertEquals(1, db.count(Genre.class));
    }

    @Test
    void shouldRollbackUpdateGenre(){
        Genre firstGenre = genreService.createGenre(new Genre("биография"));
        genreService.createGenre(new Genre("Мультфильм"));
        assertNotNull(firstGenre.getId());
        assertThrows(
                NotFoundException.class,
                ()->genreService.updateGenre(new Genre("Биография"),99L)
        );
        assertThrows(
                ExistsException.class,
                ()->genreService.updateGenre(new Genre("Мультфильм"),firstGenre.getId())
        );
        assertEquals("биография", firstGenre.getName());
    }


    @Test
    void shouldDeleteGenreSuccessfully(){
        Genre genre = genreService.createGenre(new Genre("Боевик"));
        assertNotNull(genre.getId());
        genreService.deleteGenre(genre.getId());
        assertEquals(0, db.count(Genre.class));
    }

    @Test
    void shouldRollbackDeleteGenre(){
        genreService.createGenres(
                List.of(
                        new Genre("Боевик"),
                        new Genre("Биография")
                )
        );
        assertThrows(
                NotFoundException.class,
                ()->genreService.deleteGenre(7L)
        );
        assertEquals(2, db.count(Genre.class));
    }
}
