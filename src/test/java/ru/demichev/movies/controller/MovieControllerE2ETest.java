package ru.demichev.movies.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.dto.MovieDto;
import ru.demichev.movies.dto.crt.MovieCreateDto;
import ru.demichev.movies.service.DirectorService;
import ru.demichev.movies.testUtil.E2ETest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;
import ru.demichev.movies.testUtil.TestRESTFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@E2ETest
public class MovieControllerE2ETest extends IntegrationSuite {

    @Autowired
    private TestRESTFacade rest;
    @Autowired
    private TestDBFacade db;

    private Movie firstMovie;

    private Director director;
    private Director firstDirector;

    @Autowired
    private DirectorService directorService;


    @Before
    public void before(){

    }

    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
        director = db.persist(new Director("Квентин", "Тарантино",1950));
        firstDirector = (Director) db.getById(Director.class, director.getId());
        firstMovie = db.persist(new Movie("Терминатор", "про будущее",1984,firstDirector));
        db.persist(new Movie("Джентельмены", "про мужиков",2020,firstDirector));
        db.persist(new Movie("Аватар", "про другую планету",2009,firstDirector));
    }

    @AfterEach
    void afterEach(){
        directorService.deleteDirector(director.getId());
    }


    @Test
    void shouldFindAllSucceed(){

        ResponseEntity<List<MovieDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(3, db.count(Movie.class));
    }

    @Test
    void shouldFindByIdSucceed(){

        ResponseEntity<MovieDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie/" + firstMovie.getId(),
                        HttpMethod.GET,
                        null,
                        MovieDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        MovieDto body = response.getBody();
        assertNotNull(body.getTitle());
        assertEquals("Терминатор", body.getTitle());
        assertEquals(3, db.count(Movie.class));
    }

    @Test
    void shouldFindById404(){

        ResponseEntity<MovieDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie/99",
                        HttpMethod.GET,
                        null,
                        MovieDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Movie.class));
    }

    @Test
    void shouldCreateMovieSucceed(){

        Movie movie = new Movie("Терминатор 3: Восстание машин", "про будущее",2003,firstDirector);

        ResponseEntity<MovieCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie",
                        HttpMethod.PUT,
                        new HttpEntity<>(movie),
                        MovieCreateDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        MovieCreateDto body = response.getBody();
        assertNotNull(body.getTitle());
        assertEquals("Терминатор 3: Восстание машин", body.getTitle());
        assertEquals(4, db.count(Movie.class));
    }


    @Test
    void shouldCreateMovieError409(){
        Movie Movie = new Movie("Джентельмены", "про мужиков",2020,firstDirector);
        ResponseEntity<MovieCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie",
                        HttpMethod.PUT,
                        new HttpEntity<>(Movie),
                        MovieCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Movie.class));
    }

    @Test
    void shouldUpdateMovieSucceed(){
        Movie Movie = new Movie("Терминатор-2", "про будущее",1991,firstDirector);

        ResponseEntity<MovieCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie/" + firstMovie.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(Movie),
                        MovieCreateDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        MovieCreateDto body = response.getBody();
        assertNotNull(body.getTitle());
        assertEquals("Терминатор-2", body.getTitle());
        Movie updateMovie = (Movie) db.getById(Movie.class, firstMovie.getId());
        assertEquals("Терминатор-2", updateMovie.getTitle());
        assertEquals(3, db.count(Movie.class));
    }

    @Test
    void shouldUpdateMovieError409(){
        Movie Movie = new Movie("Джентельмены", "про мужиков",2020,firstDirector);
        ResponseEntity<MovieCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie/" + firstMovie.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(Movie),
                        MovieCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Movie.class));
    }

    @Test
    void shouldUpdateMovieError404(){
        Movie Movie = new Movie("Джентельмены", "про мужиков",2020,firstDirector);
        ResponseEntity<MovieCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie/99",
                        HttpMethod.PATCH,
                        new HttpEntity<>(Movie),
                        MovieCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Movie.class));
    }

    @Test
    void shouldDeleteMovieSucceed(){
        ResponseEntity<List<MovieDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie/" + firstMovie.getId(),
                        HttpMethod.DELETE,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(2, response.getBody().size());
        assertEquals(2, db.count(Movie.class));
    }

    @Test
    void shouldDeleteMovieError404(){
        ResponseEntity<MovieDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/movie/99",
                        HttpMethod.DELETE,
                        null,
                        MovieDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Movie.class));
    }
}
