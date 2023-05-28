package ru.demichev.movies.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.service.configuration.DirectorServiceConfiguration;
import ru.demichev.movies.service.configuration.MovieServiceConfiguration;
import ru.demichev.movies.testUtil.DBTest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;

import static org.junit.jupiter.api.Assertions.*;

@DBTest
@ContextConfiguration(classes = {MovieServiceConfiguration.class, DirectorServiceConfiguration.class})
public class MovieServiceDBTest extends IntegrationSuite {

    @Autowired
    private MovieService movieService;

    @Autowired
    private DirectorService directorService;

    @Autowired
    private TestDBFacade db;
    private Director firstDirector;

    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
        firstDirector = directorService.createDirector(new Director("Квентин", "Тарантино",1950));
    }
    @AfterEach
    void afterEach(){
        directorService.deleteDirector(firstDirector.getId());
    }

    @Test
    void shouldFindAllSuccessfully(){
        db.persist(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        db.persist(new Movie("Терминатор", "про будущее",1984,firstDirector));

        movieService.findAll();
        assertEquals(2,db.count(Movie.class));
    }

    @Test
    void shouldFindByIdSuccessfully(){
        Movie firstMovie = movieService.createMovie(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        Movie secondMovie = movieService.createMovie(new Movie("Терминатор", "про будущее",1984,firstDirector));
        Movie result = movieService.findById(firstMovie.getId());
        assertNotNull(result.getId());
        assertEquals(firstMovie.getTitle(), result.getTitle());
        assertNotEquals(secondMovie.getTitle(), result.getTitle());
        assertEquals(2,db.count(Movie.class));
    }

    @Test
    void shouldRollbackFindBiId(){
        Movie firstMovie = db.persist(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        firstMovie.setId(15L);
        assertThrows(
                NotFoundException.class,
                ()-> movieService.findById(20L)
        );
        assertEquals(1, db.count(Movie.class));
    }

    @Test
    void shouldCreateSuccessfully(){
        Movie Movie = movieService.createMovie(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        assertNotNull(Movie.getId());
        assertEquals("Джентельмены", Movie.getTitle());
        assertEquals(1,db.count(Movie.class));
    }

    @Test
    void shouldRollbackCreateMovie(){
        db.persist(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        assertThrows(
                ExistsException.class,
                ()-> movieService.createMovie(new Movie("Джентельмены", "про мужиков",2019,firstDirector))
        );
        assertEquals(1, db.count(Movie.class));
    }

    @Test
    void shouldUpdateMovieSuccessfully(){
        Movie Movie = movieService.createMovie(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        assertNotNull(Movie.getId());
        Movie updatedMovie = movieService.updateMovie(new Movie("Джон", "Тарантино",1950,firstDirector), Movie.getId());
        assertEquals("Джон", updatedMovie.getTitle());
        assertEquals(1, db.count(Movie.class));
    }

    @Test
    void shouldRollbackUpdateMovie(){
        Movie firstMovie = movieService.createMovie(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        movieService.createMovie(new Movie("Терминатор", "про будущее",1984,firstDirector));
        assertNotNull(firstMovie.getId());
        assertThrows(
                NotFoundException.class,
                ()-> movieService.updateMovie(new Movie("Джон", "Тарантино",1950,firstDirector),99L)
        );
        assertThrows(
                ExistsException.class,
                ()-> movieService.updateMovie(new Movie("Терминатор", "про будущее",1984,firstDirector),firstMovie.getId())
        );
        assertEquals("Джентельмены", firstMovie.getTitle());
    }


    @Test
    void shouldDeleteMovieSuccessfully(){
        Movie Movie = movieService.createMovie(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        assertNotNull(Movie.getId());
        movieService.deleteMovie(Movie.getId());
        assertEquals(0, db.count(Movie.class));
    }

    @Test
    void shouldRollbackDeleteMovie(){
        movieService.createMovie(new Movie("Джентельмены", "про мужиков",2019,firstDirector));
        movieService.createMovie(new Movie("Терминатор", "про будущее",1984,firstDirector));
        assertThrows(
                NotFoundException.class,
                ()-> movieService.deleteMovie(7L)
        );
        assertEquals(2, db.count(Movie.class));
    }
}
