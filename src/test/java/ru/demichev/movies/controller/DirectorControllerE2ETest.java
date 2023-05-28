package ru.demichev.movies.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.dto.DirectorDto;
import ru.demichev.movies.dto.crt.DirectorCreateDto;
import ru.demichev.movies.testUtil.E2ETest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;
import ru.demichev.movies.testUtil.TestRESTFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@E2ETest
public class DirectorControllerE2ETest extends IntegrationSuite {

    @Autowired
    private TestRESTFacade rest;
    @Autowired
    private TestDBFacade db;

    private Director firstDirector;
    
    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
        firstDirector = db.persist(new Director("Квентин", "Тарантино",1963));
        db.persist(new Director("Гай", "Ричи",1968));
        db.persist(new Director("Джеймс", "Кэмерон",1954));
    }

    @Test
    void shouldFindAllSucceed(){

        ResponseEntity<List<DirectorDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(3, db.count(Director.class));
    }

    @Test
    void shouldFindByIdSucceed(){

        ResponseEntity<DirectorDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director/" + firstDirector.getId(),
                        HttpMethod.GET,
                        null,
                        DirectorDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        DirectorDto body = response.getBody();
        assertNotNull(body.getFirstName());
        assertEquals("Квентин", body.getFirstName());
        assertEquals(3, db.count(Director.class));
    }

    @Test
    void shouldFindById404(){

        ResponseEntity<DirectorDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director/99",
                        HttpMethod.GET,
                        null,
                        DirectorDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Director.class));
    }

    @Test
    void shouldCreateDirectorSucceed(){
        Director director = new Director("Люк", "Бессон",1959);

        ResponseEntity<DirectorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director",
                        HttpMethod.PUT,
                        new HttpEntity<>(director),
                        DirectorCreateDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        DirectorCreateDto body = response.getBody();
        assertNotNull(body.getFirstName());
        assertEquals("Люк", body.getFirstName());
        assertEquals(4, db.count(Director.class));
    }

    @Test
    void shouldCreateDirectorError409(){
        Director Director = new Director("Гай", "Ричи",1968);
        ResponseEntity<DirectorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director",
                        HttpMethod.PUT,
                        new HttpEntity<>(Director),
                        DirectorCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Director.class));
    }

    @Test
    void shouldUpdateDirectorSucceed(){
        Director Director = new Director("Квен", "Тарантино",1963);

        ResponseEntity<DirectorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director/" + firstDirector.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(Director),
                        DirectorCreateDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        DirectorCreateDto body = response.getBody();
        assertNotNull(body.getFirstName());
        assertEquals("Квен", body.getFirstName());
        Director updateDirector = (Director) db.getById(Director.class, firstDirector.getId());
        assertEquals("Квен", updateDirector.getFirstName());
        assertEquals(3, db.count(Director.class));
    }

    @Test
    void shouldUpdateDirectorError409(){
        Director Director = new Director("Гай", "Ричи",1968);
        ResponseEntity<DirectorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director/" + firstDirector.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(Director),
                        DirectorCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Director.class));
    }

    @Test
    void shouldUpdateDirectorError404(){
        Director Director = new Director("Гай", "Ричи",1968);
        ResponseEntity<DirectorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director/99",
                        HttpMethod.PATCH,
                        new HttpEntity<>(Director),
                        DirectorCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Director.class));
    }

    @Test
    void shouldDeleteDirectorSucceed(){
        ResponseEntity<List<DirectorDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director/" + firstDirector.getId(),
                        HttpMethod.DELETE,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(2, response.getBody().size());
        assertEquals(2, db.count(Director.class));
    }

    @Test
    void shouldDeleteDirectorError404(){
        ResponseEntity<DirectorDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/director/99",
                        HttpMethod.DELETE,
                        null,
                        DirectorDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Director.class));
    }
}
