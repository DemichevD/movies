package ru.demichev.movies.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.dto.GenreDto;
import ru.demichev.movies.dto.crt.GenreCreateDto;
import ru.demichev.movies.testUtil.E2ETest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;
import ru.demichev.movies.testUtil.TestRESTFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@E2ETest
public class GenreControllerE2ETest extends IntegrationSuite {
    @Autowired
    private TestRESTFacade rest;
    @Autowired
    private TestDBFacade db;

    private Genre firstGenre;
    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
        firstGenre = db.persist(new Genre("Фантастика"));
        db.persist(new Genre("Боевик"));
        db.persist(new Genre("Биография"));
    }

    @Test
    void shouldFindAllSucceed(){

        ResponseEntity<List<GenreDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldFindByIdSucceed(){

        ResponseEntity<GenreDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/" + firstGenre.getId(),
                        HttpMethod.GET,
                        null,
                        GenreDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        GenreDto body = response.getBody();
        assertNotNull(body.getName());
        assertEquals("Фантастика", body.getName());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldFindById404(){

        ResponseEntity<GenreDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/99",
                        HttpMethod.GET,
                        null,
                        GenreDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldFindByNameSucceed(){

        ResponseEntity<GenreDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/name?name=Фантастика",
                        HttpMethod.GET,
                        null,
                        GenreDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        GenreDto body = response.getBody();
        assertNotNull(body.getName());
        assertEquals("Фантастика", body.getName());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldFindByNameError404(){

        ResponseEntity<GenreDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/name?name=Приключения",
                        HttpMethod.GET,
                        null,
                        GenreDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldFindByPrefixSucceed(){

        ResponseEntity<List<GenreDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/names?prefix=б",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldCreateGenreSucceed(){
        Genre genre = new Genre("Приключения");

        ResponseEntity<GenreCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre",
                        HttpMethod.PUT,
                        new HttpEntity<>(genre),
                        GenreCreateDto.class
        );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        GenreCreateDto body = response.getBody();
        assertNotNull(body.getName());
        assertEquals("Приключения", body.getName());
        assertEquals(4, db.count(Genre.class));
    }

    @Test
    void shouldCreateGenreError409(){
        Genre genre = new Genre("Боевик");
        ResponseEntity<GenreCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre",
                        HttpMethod.PUT,
                        new HttpEntity<>(genre),
                        GenreCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldCreateGenresSucceed(){
        List<Genre> genres = List.of(
                new Genre("Приключения"),
                new Genre("Ужасы")
        );
        ResponseEntity<List<GenreDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/new_genres",
                        HttpMethod.PUT,
                        new HttpEntity<>(genres),
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(5, db.count(Genre.class));
    }

    @Test
    void shouldCreateGenresError409(){
        List<Genre> genres = List.of(
                new Genre("Приключения"),
                new Genre("Боевик")
        );

        ResponseEntity<GenreDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/new_genres",
                        HttpMethod.PUT,
                        new HttpEntity<>(genres),
                        GenreDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldUpdateGenreSucceed(){
        Genre genre = new Genre("Супер_Фантаст");

        ResponseEntity<GenreCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/" + firstGenre.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(genre),
                        GenreCreateDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        GenreCreateDto body = response.getBody();
        assertNotNull(body.getName());
        assertEquals("Супер_Фантаст", body.getName());
        Genre updateGenre = (Genre) db.getById(Genre.class, firstGenre.getId());
        assertEquals("Супер_Фантаст", updateGenre.getName());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldUpdateGenreError409(){
        Genre genre = new Genre("Боевик");
        ResponseEntity<GenreCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/" + firstGenre.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(genre),
                        GenreCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldUpdateGenreError404(){
        Genre genre = new Genre("Боевик");
        ResponseEntity<GenreCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/99",
                        HttpMethod.PATCH,
                        new HttpEntity<>(genre),
                        GenreCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Genre.class));
    }

    @Test
    void shouldDeleteGenreSucceed(){
        ResponseEntity<List<GenreDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/" + firstGenre.getId(),
                        HttpMethod.DELETE,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(2, response.getBody().size());
        assertEquals(2, db.count(Genre.class));
    }

    @Test
    void shouldDeleteGenreError404(){
        ResponseEntity<GenreDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/genre/99",
                        HttpMethod.DELETE,
                        null,
                        GenreDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Genre.class));
    }

}
