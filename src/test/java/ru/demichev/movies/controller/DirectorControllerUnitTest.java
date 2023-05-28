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
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.dto.DirectorDto;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.DirectorMapper;
import ru.demichev.movies.security.RoleCheckService;
import ru.demichev.movies.service.DirectorService;
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
@WebMvcTest(DirectorController.class)
@ComponentScan(basePackages = {"ru.demichev.movies.exceptions"})
public class DirectorControllerUnitTest {

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
    private DirectorService directorService;

    @MockBean
    private DirectorMapper directorMapper;

    private static final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenFindAllShouldReturn200AndDirectorsReturned() throws Exception {
        DirectorDto firstDirector = new DirectorDto();
        firstDirector.setFirstName("Квентин");
        firstDirector.setLastName("Тарантино");
        firstDirector.setYearOfBirth(1963);
        firstDirector.setId(1L);
        DirectorDto secondDirector = new DirectorDto();
        secondDirector.setFirstName("Гай");
        secondDirector.setLastName("Ричи");
        firstDirector.setYearOfBirth(1968);
        secondDirector.setId(2L);

        doReturn(Arrays.asList(new Director(), new Director())).when(directorService).findAll();
        doReturn(firstDirector).doReturn(secondDirector).when(directorMapper).toDto(any(Director.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/director")
                                .with(user("user").roles("VIEWER"))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(List.of(mapper.writeValueAsString(firstDirector), mapper.writeValueAsString(secondDirector)).toString()))
        ;
    }
    @Test
    public void whenFindByIdShouldReturn404() throws Exception{
        when(directorService.findById(15L)).thenThrow(new NotFoundException("1"));

        this.mockMvc.perform(
                        get("/api/v1/movies/director/15")
                                .with(user("user").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenFindByIdShouldReturn200AndDirectorReturned() throws Exception{
        DirectorDto firstDirector = new DirectorDto();
        firstDirector.setFirstName("Квентин");
        firstDirector.setLastName("Тарантино");
        firstDirector.setYearOfBirth(1963);
        firstDirector.setId(15L);
        doReturn(new Director()).when(directorService).findById(anyLong());
        doReturn(firstDirector).when(directorMapper).toDto(any(Director.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/director/15")
                                .with(user("user").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(firstDirector)))
        ;
    }

    @Test
    public void whenCreateDirectorShouldReturn409() throws Exception{
        DirectorDto director = new DirectorDto();
        director.setFirstName("Квентин");
        director.setLastName("Тарантино");
        director.setYearOfBirth(1963);
        director.setId(15L);
        String json = mapper.writeValueAsString(director);
        doThrow(new ExistsException("1")).when(directorService).createDirector(any());

        this.mockMvc.perform(
                        put("/api/v1/movies/director")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenCreateDirectorShouldReturn200AndDirectorReturned() throws Exception{
        DirectorDto director = new DirectorDto();
        director.setFirstName("Квентин");
        director.setLastName("Тарантино");
        director.setYearOfBirth(1963);
        director.setId(15L);
        doReturn(new Director()).when(directorService).createDirector(any());
        doReturn(director).when(directorMapper).toDto(any(Director.class));
        String json = mapper.writeValueAsString(director);

        this.mockMvc.perform(
                        put("/api/v1/movies/director")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(director)))
        ;

        this.mockMvc.perform(
                        get("/api/v1/movies/director/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    public void whenUpdateDirectorShouldReturn409() throws Exception{
        DirectorDto director = new DirectorDto();
        director.setFirstName("Гай");
        director.setLastName("Ричи");
        director.setYearOfBirth(1968);
        director.setId(15L);
        String json = mapper.writeValueAsString(director);
        doThrow(new ExistsException("1")).when(directorService).updateDirector(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/director/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenUpdateDirectorShouldReturn404() throws Exception{
        DirectorDto director = new DirectorDto();
        director.setFirstName("Гай");
        director.setLastName("Ричи");
        director.setYearOfBirth(1968);
        director.setId(5L);
        String json = mapper.writeValueAsString(director);
        doThrow(new NotFoundException("1")).when(directorService).updateDirector(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/director/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenUpdateDirectorShouldReturn200AndDirectorReturned() throws Exception{
        DirectorDto director = new DirectorDto();
        director.setFirstName("Гай");
        director.setLastName("Ричи");
        director.setYearOfBirth(1968);
        director.setId(15L);
        doReturn(new Director()).when(directorService).updateDirector(any(),anyLong());
        doReturn(director).when(directorMapper).toDto(any(Director.class));
        String json = mapper.writeValueAsString(director);

        this.mockMvc.perform(
                        patch("/api/v1/movies/director/15")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(director)))
        ;



        this.mockMvc.perform(
                        get("/api/v1/movies/director/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }
    @Test
    public void whenDeletedDirectorShouldReturn404() throws Exception{
        DirectorDto director = new DirectorDto();
        director.setFirstName("Гай");
        director.setLastName("Ричи");
        director.setYearOfBirth(1968);
        director.setId(15L);
        String json = mapper.writeValueAsString(director);
        doThrow(new NotFoundException("1")).when(directorService).deleteDirector(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/director/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenDeleteDirectorShouldReturn200AndDirectorReturned() throws Exception{
        DirectorDto director = new DirectorDto();
        director.setFirstName("Гай");
        director.setLastName("Ричи");
        director.setYearOfBirth(1968);
        director.setId(15L);
        doReturn(List.of(new Actor())).when(directorService).deleteDirector(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/director/2")
                                .with(user("admin").roles("EDITOR"))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
        ;
    }
}

