package ru.demichev.movies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.service.configuration.ActorServiceConfiguration;
import ru.demichev.movies.testUtil.DBTest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;

import static org.junit.jupiter.api.Assertions.*;

@DBTest
@ContextConfiguration(classes = ActorServiceConfiguration.class)
public class ActorServiceDBTest extends IntegrationSuite {

    @Autowired
    private ActorService actorService;
    @Autowired
    private TestDBFacade db;

    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
    }

    @Test
    void shouldFindAllSuccessfully(){
        db.persist(new Actor("Джон", "Траволта",1950));
        db.persist(new Actor("Колин", "Фаррел",1950));
        actorService.findAll();
        assertEquals(2,db.count(Actor.class));
    }

    @Test
    void shouldFindByIdSuccessfully(){
        Actor firstActor = actorService.createActor(new Actor("Джон", "Траволта",1950));
        Actor secondActor = actorService.createActor(new Actor("Колин", "Фаррел",1950));
        Actor result = actorService.findById(firstActor.getId());
        assertNotNull(result.getId());
        assertEquals(firstActor.getFirstName(), result.getFirstName());
        assertNotEquals(secondActor.getFirstName(), result.getFirstName());
        assertEquals(2,db.count(Actor.class));
    }

    @Test
    void shouldRollbackFindBiId(){
        Actor firstActor = db.persist(new Actor("Джон", "Траволта",1950));
        firstActor.setId(15L);
        assertThrows(
                NotFoundException.class,
                ()-> actorService.findById(20L)
        );
        assertEquals(1, db.count(Actor.class));
    }

    @Test
    void shouldCreateSuccessfully(){
        Actor Actor = actorService.createActor(new Actor("Джон", "Траволта",1950));
        assertNotNull(Actor.getId());
        assertEquals("Джон", Actor.getFirstName());
        assertEquals(1,db.count(Actor.class));
    }

    @Test
    void shouldRollbackCreateActor(){
        db.persist(new Actor("Джон", "Траволта",1950));
        assertThrows(
                ExistsException.class,
                ()-> actorService.createActor(new Actor("Джон", "Траволта",1950))
        );
        assertEquals(1, db.count(Actor.class));
    }

    @Test
    void shouldUpdateActorSuccessfully(){
        Actor Actor = actorService.createActor(new Actor("Джон", "Траволта",1950));
        assertNotNull(Actor.getId());
        Actor updatedActor = actorService.updateActor(new Actor("Джон", "Тарантино",1950), Actor.getId());
        assertEquals("Джон", updatedActor.getFirstName());
        assertEquals(1, db.count(Actor.class));
    }

    @Test
    void shouldRollbackUpdateActor(){
        Actor firstActor = actorService.createActor(new Actor("Джон", "Траволта",1950));
        actorService.createActor(new Actor("Колин", "Фаррел",1950));
        assertNotNull(firstActor.getId());
        assertThrows(
                NotFoundException.class,
                ()-> actorService.updateActor(new Actor("Джон", "Тарантино",1950),99L)
        );
        assertThrows(
                ExistsException.class,
                ()-> actorService.updateActor(new Actor("Колин", "Фаррел",1950),firstActor.getId())
        );
        assertEquals("Джон", firstActor.getFirstName());
    }


    @Test
    void shouldDeleteActorSuccessfully(){
        Actor Actor = actorService.createActor(new Actor("Джон", "Траволта",1950));
        assertNotNull(Actor.getId());
        actorService.deleteActor(Actor.getId());
        assertEquals(0, db.count(Actor.class));
    }

    @Test
    void shouldRollbackDeleteActor(){
        actorService.createActor(new Actor("Джон", "Траволта",1950));
        actorService.createActor(new Actor("Колин", "Фаррел",1950));
        assertThrows(
                NotFoundException.class,
                ()-> actorService.deleteActor(7L)
        );
        assertEquals(2, db.count(Actor.class));
    }
}
