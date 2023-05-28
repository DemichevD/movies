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
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.dto.MovieDto;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.MovieMapper;
import ru.demichev.movies.security.RoleCheckService;
import ru.demichev.movies.service.MovieService;
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
@WebMvcTest(MovieController.class)
@ComponentScan(basePackages = {"ru.demichev.movies.exceptions"})
public class MovieControllerUnitTest {


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
    private MovieService movieService;
    @MockBean
    private MovieMapper movieMapper;

    private static final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenFindAllShouldReturn200AndMoviesReturned() throws Exception {
        MovieDto firstMovie = new MovieDto();
        firstMovie.setTitle("Терминатор");
        firstMovie.setDescription("про будущее");
        firstMovie.setYear(1984);
        firstMovie.setId(1L);
        MovieDto secondMovie = new MovieDto();
        secondMovie.setTitle("Джентельмены");
        secondMovie.setDescription("про мужиков");
        firstMovie.setYear(2020);
        secondMovie.setId(2L);

        doReturn(Arrays.asList(new Movie(), new Movie())).when(movieService).findAll();
        doReturn(firstMovie).doReturn(secondMovie).when(movieMapper).toDto(any(Movie.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/movie")
                                .with(user("user").roles("VIEWER"))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(List.of(mapper.writeValueAsString(firstMovie), mapper.writeValueAsString(secondMovie)).toString()))
        ;
    }
    @Test
    public void whenFindByIdShouldReturn404() throws Exception{
        when(movieService.findById(15L)).thenThrow(new NotFoundException("1"));

        this.mockMvc.perform(
                        get("/api/v1/movies/movie/15")
                                .with(user("user").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenFindByIdShouldReturn200AndMovieReturned() throws Exception{
        MovieDto firstMovie = new MovieDto();
        firstMovie.setTitle("Терминатор");
        firstMovie.setDescription("про будущее");
        firstMovie.setYear(1984);
        firstMovie.setId(15L);
        doReturn(new Movie()).when(movieService).findById(anyLong());
        doReturn(firstMovie).when(movieMapper).toDto(any(Movie.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/movie/15")
                                .with(user("user").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(firstMovie)))
        ;
    }

    @Test
    public void whenCreateMovieShouldReturn409() throws Exception{
        MovieDto movie = new MovieDto();
        movie.setTitle("Терминатор");
        movie.setDescription("про будущее");
        movie.setYear(1984);
        movie.setId(15L);
        String json = mapper.writeValueAsString(movie);
        doThrow(new ExistsException("1")).when(movieService).createMovie(any());

        this.mockMvc.perform(
                        put("/api/v1/movies/movie")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenCreateMovieShouldReturn200AndMovieReturned() throws Exception{
        MovieDto movie = new MovieDto();
        movie.setTitle("Терминатор");
        movie.setDescription("про будущее");
        movie.setYear(1984);
        doReturn(new Movie()).when(movieService).createMovie(any());
        doReturn(movie).when(movieMapper).toDto(any(Movie.class));
        String json = mapper.writeValueAsString(movie);

        this.mockMvc.perform(
                        put("/api/v1/movies/movie")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(movie)))
        ;

        this.mockMvc.perform(
                        get("/api/v1/movies/movie/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @Test
    public void whenUpdateMovieShouldReturn409() throws Exception{
        MovieDto movie = new MovieDto();
        movie.setTitle("Джентельмены");
        movie.setDescription("про мужиков");
        movie.setYear(2020);
        movie.setId(15L);
        String json = mapper.writeValueAsString(movie);
        doThrow(new ExistsException("1")).when(movieService).updateMovie(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/movie/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenUpdateMovieShouldReturn404() throws Exception{
        MovieDto movie = new MovieDto();
        movie.setTitle("Джентельмены");
        movie.setDescription("про мужиков");
        movie.setYear(2020);
        movie.setId(5L);
        String json = mapper.writeValueAsString(movie);
        doThrow(new NotFoundException("1")).when(movieService).updateMovie(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/movie/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenUpdateMovieShouldReturn200AndMovieReturned() throws Exception{
        MovieDto movie = new MovieDto();
        movie.setTitle("Джентельмены");
        movie.setDescription("про мужиков");
        movie.setYear(2020);
        movie.setId(15L);
        doReturn(new Movie()).when(movieService).updateMovie(any(),anyLong());
        doReturn(movie).when(movieMapper).toDto(any(Movie.class));
        String json = mapper.writeValueAsString(movie);

        this.mockMvc.perform(
                        patch("/api/v1/movies/movie/15")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(movie)))
        ;



        this.mockMvc.perform(
                        get("/api/v1/movies/movie/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }
    @Test
    public void whenDeletedMovieShouldReturn404() throws Exception{
        MovieDto movie = new MovieDto();
        movie.setTitle("Джентельмены");
        movie.setDescription("про мужиков");
        movie.setYear(2020);
        movie.setId(15L);
        String json = mapper.writeValueAsString(movie);
        doThrow(new NotFoundException("1")).when(movieService).deleteMovie(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/movie/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenDeleteMovieShouldReturn200AndMovieReturned() throws Exception{
        MovieDto movie = new MovieDto();
        movie.setTitle("Джентельмены");
        movie.setDescription("про мужиков");
        movie.setYear(2020);
        movie.setId(15L);
        doReturn(List.of(new Actor())).when(movieService).deleteMovie(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/movie/2")
                                .with(user("admin").roles("EDITOR"))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
        ;
    }
}
