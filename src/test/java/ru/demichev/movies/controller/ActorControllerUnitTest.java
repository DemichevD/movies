package ru.demichev.movies.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.dto.ActorDto;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.ActorMapper;
import ru.demichev.movies.security.RoleCheckService;
import ru.demichev.movies.service.ActorService;
import ru.demichev.movies.service.UserAuthService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebMvcTest(ActorController.class)
@ComponentScan(basePackages = {"ru.demichev.movies.exceptions"})
public class ActorControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    UserAuthService userAuthService;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    RoleCheckService roleCheckService;
    @MockBean
    private ActorService actorService;

    @MockBean
    private ActorMapper actorMapper;

    private static final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenFindAllShouldReturn200AndActorsReturned() throws Exception {
        ActorDto firstActor = new ActorDto();
        firstActor.setFirstName("Джон");
        firstActor.setLastName("Траволта");
        firstActor.setYearOfBirth(1954);
        firstActor.setId(1L);
        ActorDto secondActor = new ActorDto();
        secondActor.setFirstName("Колин");
        secondActor.setLastName("Фаррел");
        firstActor.setYearOfBirth(1976);
        secondActor.setId(2L);

        doReturn(Arrays.asList(new Actor(), new Actor())).when(actorService).findAll();
        doReturn(firstActor).doReturn(secondActor).when(actorMapper).toDto(any(Actor.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/actor")
                                .with(user("user").roles("VIEWER"))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(List.of(mapper.writeValueAsString(firstActor), mapper.writeValueAsString(secondActor)).toString()))
        ;
    }
    @Test
    public void whenFindByIdShouldReturn404() throws Exception{
        when(actorService.findById(15L)).thenThrow(new NotFoundException("1"));

        this.mockMvc.perform(
                        get("/api/v1/movies/actor/15")
                                .with(user("user").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenFindByIdShouldReturn200AndActorReturned() throws Exception{
        ActorDto firstActor = new ActorDto();
        firstActor.setFirstName("Джон");
        firstActor.setLastName("Траволта");
        firstActor.setYearOfBirth(1954);
        firstActor.setId(15L);
        doReturn(new Actor()).when(actorService).findById(anyLong());
        doReturn(firstActor).when(actorMapper).toDto(any(Actor.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/actor/15")
                                .with(user("user").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(firstActor)))
        ;
    }

    @Test
    public void whenCreateActorShouldReturn409() throws Exception{
        ActorDto actor = new ActorDto();
        actor.setFirstName("Джон");
        actor.setLastName("Траволта");
        actor.setYearOfBirth(1954);
        actor.setId(15L);
        String json = mapper.writeValueAsString(actor);
        doThrow(new ExistsException("1")).when(actorService).createActor(any());

        this.mockMvc.perform(
                        put("/api/v1/movies/actor")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenCreateActorShouldReturn200AndActorReturned() throws Exception{
        ActorDto actor = new ActorDto();
        actor.setFirstName("Джон");
        actor.setLastName("Траволта");
        actor.setYearOfBirth(1954);
        actor.setId(15L);
        doReturn(new Actor()).when(actorService).createActor(any());
        doReturn(actor).when(actorMapper).toDto(any(Actor.class));
        String json = mapper.writeValueAsString(actor);

        this.mockMvc.perform(
                        put("/api/v1/movies/actor")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(actor)))
        ;

        this.mockMvc.perform(
                        get("/api/v1/movies/actor/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    public void whenUpdateActorShouldReturn409() throws Exception{
        ActorDto actor = new ActorDto();
        actor.setFirstName("Колин");
        actor.setLastName("Фаррел");
        actor.setYearOfBirth(1976);
        actor.setId(15L);
        String json = mapper.writeValueAsString(actor);
        doThrow(new ExistsException("1")).when(actorService).updateActor(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/actor/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenUpdateActorShouldReturn404() throws Exception{
        ActorDto actor = new ActorDto();
        actor.setFirstName("Колин");
        actor.setLastName("Фаррел");
        actor.setYearOfBirth(1976);
        actor.setId(5L);
        String json = mapper.writeValueAsString(actor);
        doThrow(new NotFoundException("1")).when(actorService).updateActor(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/actor/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenUpdateActorShouldReturn200AndActorReturned() throws Exception{
        ActorDto actor = new ActorDto();
        actor.setFirstName("Колин");
        actor.setLastName("Фаррел");
        actor.setYearOfBirth(1976);
        actor.setId(15L);
        doReturn(new Actor()).when(actorService).updateActor(any(),anyLong());
        doReturn(actor).when(actorMapper).toDto(any(Actor.class));
        String json = mapper.writeValueAsString(actor);

        this.mockMvc.perform(
                        patch("/api/v1/movies/actor/15")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(actor)))
        ;



        this.mockMvc.perform(
                        get("/api/v1/movies/actor/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }
    @Test
    public void whenDeletedActorShouldReturn404() throws Exception{
        ActorDto actor = new ActorDto();
        actor.setFirstName("Колин");
        actor.setLastName("Фаррел");
        actor.setYearOfBirth(1976);
        actor.setId(15L);
        String json = mapper.writeValueAsString(actor);
        doThrow(new NotFoundException("1")).when(actorService).deleteActor(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/actor/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenDeleteActorShouldReturn200AndActorReturned() throws Exception{
        ActorDto actor = new ActorDto();
        actor.setFirstName("Колин");
        actor.setLastName("Фаррел");
        actor.setYearOfBirth(1976);
        actor.setId(15L);
        doReturn(List.of(new Actor())).when(actorService).deleteActor(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/actor/2")
                                .with(user("admin").roles("EDITOR"))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
        ;
    }
}
