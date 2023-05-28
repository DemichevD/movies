package ru.demichev.movies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.service.configuration.DirectorServiceConfiguration;
import ru.demichev.movies.testUtil.DBTest;
import ru.demichev.movies.testUtil.IntegrationSuite;
import ru.demichev.movies.testUtil.TestDBFacade;

import static org.junit.jupiter.api.Assertions.*;

@DBTest
@ContextConfiguration(classes = DirectorServiceConfiguration.class)
public class DirectorServiceDBTest extends IntegrationSuite {

    @Autowired
    private DirectorService directorService;
    @Autowired
    private TestDBFacade db;

    @BeforeEach
    void beforeEach(){
        db.cleanDatabase();
    }

    @Test
    void shouldFindAllSuccessfully(){
        db.persist(new Director("Квентин", "Тарантино",1950));
        db.persist(new Director("Гай", "Ричи",1950));
        directorService.findAll();
        assertEquals(2,db.count(Director.class));
    }

    @Test
    void shouldFindByIdSuccessfully(){
        Director firstDirector = directorService.createDirector(new Director("Квентин", "Тарантино",1950));
        Director secondDirector = directorService.createDirector(new Director("Гай", "Ричи",1950));
        Director result = directorService.findById(firstDirector.getId());
        assertNotNull(result.getId());
        assertEquals(firstDirector.getFirstName(), result.getFirstName());
        assertNotEquals(secondDirector.getFirstName(), result.getFirstName());
        assertEquals(2,db.count(Director.class));
    }

    @Test
    void shouldRollbackFindBiId(){
        Director firstDirector = db.persist(new Director("Квентин", "Тарантино",1950));
        firstDirector.setId(15L);
        assertThrows(
                NotFoundException.class,
                ()-> directorService.findById(20L)
        );
        assertEquals(1, db.count(Director.class));
    }

    @Test
    void shouldCreateSuccessfully(){
        Director director = directorService.createDirector(new Director("Квентин", "Тарантино",1950));
        assertNotNull(director.getId());
        assertEquals("Квентин", director.getFirstName());
        assertEquals(1,db.count(Director.class));
    }

    @Test
    void shouldRollbackCreateDirector(){
        db.persist(new Director("Квентин", "Тарантино",1950));
        assertThrows(
                ExistsException.class,
                ()-> directorService.createDirector(new Director("Квентин", "Тарантино",1950))
        );
        assertEquals(1, db.count(Director.class));
    }

    @Test
    void shouldUpdateDirectorSuccessfully(){
        Director director = directorService.createDirector(new Director("Квентин", "Тарантино",1950));
        assertNotNull(director.getId());
        Director updatedDirector = directorService.updateDirector(new Director("Джон", "Тарантино",1950), director.getId());
        assertEquals("Джон", updatedDirector.getFirstName());
        assertEquals(1, db.count(Director.class));
    }

    @Test
    void shouldRollbackUpdateDirector(){
        Director firstDirector = directorService.createDirector(new Director("Квентин", "Тарантино",1950));
        directorService.createDirector(new Director("Гай", "Ричи",1950));
        assertNotNull(firstDirector.getId());
        assertThrows(
                NotFoundException.class,
                ()-> directorService.updateDirector(new Director("Джон", "Тарантино",1950),99L)
        );
        assertThrows(
                ExistsException.class,
                ()-> directorService.updateDirector(new Director("Гай", "Ричи",1950),firstDirector.getId())
        );
        assertEquals("Квентин", firstDirector.getFirstName());
    }


    @Test
    void shouldDeleteDirectorSuccessfully(){
        Director Director = directorService.createDirector(new Director("Квентин", "Тарантино",1950));
        assertNotNull(Director.getId());
        directorService.deleteDirector(Director.getId());
        assertEquals(0, db.count(Director.class));
    }

    @Test
    void shouldRollbackDeleteDirector(){
        directorService.createDirector(new Director("Квентин", "Тарантино",1950));
        directorService.createDirector(new Director("Гай", "Ричи",1950));
        assertThrows(
                NotFoundException.class,
                ()-> directorService.deleteDirector(7L)
        );
        assertEquals(2, db.count(Director.class));
    }
}
