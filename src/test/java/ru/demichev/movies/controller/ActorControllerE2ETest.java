package ru.demichev.movies.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.dto.ActorDto;
import ru.demichev.movies.dto.crt.ActorCreateDto;
import ru.demichev.movies.testUtil.E2ETest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;
import ru.demichev.movies.testUtil.TestRESTFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@E2ETest
public class ActorControllerE2ETest extends IntegrationSuite {

    @Autowired
    private TestRESTFacade rest;
    @Autowired
    private TestDBFacade db;

    private Actor firstActor;

    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
        firstActor = db.persist(new Actor("Джон", "Траволта",1950));
        db.persist(new Actor("Колин", "Фаррел",1976));
        db.persist(new Actor("Сильвестр", "Сталлоне",1946));
    }

    @Test
    void shouldFindAllSucceed(){

        ResponseEntity<List<ActorDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(3, db.count(Actor.class));
    }

    @Test
    void shouldFindByIdSucceed(){

        ResponseEntity<ActorDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor/" + firstActor.getId(),
                        HttpMethod.GET,
                        null,
                        ActorDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        ActorDto body = response.getBody();
        assertNotNull(body.getFirstName());
        assertEquals("Джон", body.getFirstName());
        assertEquals(3, db.count(Actor.class));
    }

    @Test
    void shouldFindById404(){

        ResponseEntity<ActorDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor/99",
                        HttpMethod.GET,
                        null,
                        ActorDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Actor.class));
    }

    @Test
    void shouldCreateActorSucceed(){
        Actor actor = new Actor("Арнольд", "Шварценеггер",1947);

        ResponseEntity<ActorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor",
                        HttpMethod.PUT,
                        new HttpEntity<>(actor),
                        ActorCreateDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        ActorCreateDto body = response.getBody();
        assertNotNull(body.getFirstName());
        assertEquals("Арнольд", body.getFirstName());
        assertEquals(4, db.count(Actor.class));
    }

    @Test
    void shouldCreateActorError409(){
        Actor Actor = new Actor("Колин", "Фаррел",1976);
        ResponseEntity<ActorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor",
                        HttpMethod.PUT,
                        new HttpEntity<>(Actor),
                        ActorCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Actor.class));
    }

    @Test
    void shouldUpdateActorSucceed(){
        Actor Actor = new Actor("Джонатан", "Траволта",1950);

        ResponseEntity<ActorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor/" + firstActor.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(Actor),
                        ActorCreateDto.class
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        ActorCreateDto body = response.getBody();
        assertNotNull(body.getFirstName());
        assertEquals("Джонатан", body.getFirstName());
        Actor updateActor = (Actor) db.getById(Actor.class, firstActor.getId());
        assertEquals("Джонатан", updateActor.getFirstName());
        assertEquals(3, db.count(Actor.class));
    }

    @Test
    void shouldUpdateActorError409(){
        Actor Actor = new Actor("Колин", "Фаррел",1976);
        ResponseEntity<ActorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor/" + firstActor.getId(),
                        HttpMethod.PATCH,
                        new HttpEntity<>(Actor),
                        ActorCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Actor.class));
    }

    @Test
    void shouldUpdateActorError404(){
        Actor Actor = new Actor("Колин", "Фаррел",1976);
        ResponseEntity<ActorCreateDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor/99",
                        HttpMethod.PATCH,
                        new HttpEntity<>(Actor),
                        ActorCreateDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Actor.class));
    }

    @Test
    void shouldDeleteActorSucceed(){
        ResponseEntity<List<ActorDto>> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor/" + firstActor.getId(),
                        HttpMethod.DELETE,
                        null,
                        new ParameterizedTypeReference<>(){}
                );
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(2, response.getBody().size());
        assertEquals(2, db.count(Actor.class));
    }

    @Test
    void shouldDeleteActorError404(){
        ResponseEntity<ActorDto> response = rest
                .withBasicAuth("admin", "123")
                .exchange(
                        "/api/v1/movies/actor/99",
                        HttpMethod.DELETE,
                        null,
                        ActorDto.class
                );
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(3, db.count(Actor.class));
    }
}
