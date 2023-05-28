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
import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.dto.GenreDto;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.GenreMapper;
import ru.demichev.movies.security.RoleCheckService;
import ru.demichev.movies.service.GenreService;
import ru.demichev.movies.service.UserAuthService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
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
@WebMvcTest(GenreController.class)
@ComponentScan(basePackages = {"ru.demichev.movies.exceptions"})
class GenreControllerUnitTest {
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
    private GenreService genreService;

    @MockBean
    private GenreMapper genreMapper;

    private static final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenFindAllShouldReturn200AndGenresReturned() throws Exception {
        GenreDto firstGenre = new GenreDto();
        firstGenre.setName("Боевик");
        firstGenre.setId(1L);
        GenreDto secondGenre = new GenreDto();
        secondGenre.setName("Мультфильм");
        secondGenre.setId(2L);
        doReturn(Arrays.asList(new Genre(), new Genre())).when(genreService).findAll();
        doReturn(firstGenre).doReturn(secondGenre).when(genreMapper).toDto(any(Genre.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/genre")
                                .with(user("admin").roles("VIEWER"))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(List.of(mapper.writeValueAsString(firstGenre), mapper.writeValueAsString(secondGenre)).toString()))
        ;
    }
    @Test
    public void whenFindByIdShouldReturn404() throws Exception{
        when(genreService.findById(15L)).thenThrow(new NotFoundException("1"));

        this.mockMvc.perform(
                get("/api/v1/movies/genre/15")
                        .with(user("admin").roles("VIEWER"))
        ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenFindByIdShouldReturn200AndGenreReturned() throws Exception{
        GenreDto firstGenre = new GenreDto();
        firstGenre.setName("Боевик");
        firstGenre.setId(15L);
        doReturn(new Genre()).when(genreService).findById(anyLong());
        doReturn(firstGenre).when(genreMapper).toDto(any(Genre.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/genre/15")
                                .with(user("admin").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(firstGenre)))
        ;
    }
    @Test
    public void whenFindByNameShouldReturn404() throws Exception{
        when(genreService.findByName("Ужасы")).thenThrow(new NotFoundException("1"));

        this.mockMvc.perform(
                        get("/api/v1/movies/genre/name")
                                .param("name","Ужасы")
                                .with(user("admin").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenFindByNameShouldReturn200AndGenreReturned() throws Exception{
        GenreDto firstGenre = new GenreDto();
        firstGenre.setName("Боевик");
        firstGenre.setId(15L);
        doReturn(new Genre()).when(genreService).findByName(anyString());
        doReturn(firstGenre).when(genreMapper).toDto(any(Genre.class));

        this.mockMvc.perform(
                        get("/api/v1/movies/genre/name")
                                .param("name", "Боевик")
                                .with(user("admin").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(firstGenre)))
        ;
    }
    @Test
    public void whenFindByPrefixShouldReturn200AndGenresReturned() throws Exception{
        GenreDto firstGenre = new GenreDto();
        firstGenre.setName("Боевик");
        firstGenre.setId(1L);
        GenreDto secondGenre = new GenreDto();
        secondGenre.setName("Биография");
        secondGenre.setId(2L);
        doReturn(Arrays.asList(new Genre(), new Genre())).when(genreService).findByContainingPrefix("б");
        doReturn(firstGenre).doReturn(secondGenre).when(genreMapper).toDto(any(Genre.class));
        this.mockMvc.perform(
                        get("/api/v1/movies/genre/names")
                                .param("prefix", "б")
                                .with(user("admin").roles("VIEWER"))
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(List.of(mapper.writeValueAsString(firstGenre), mapper.writeValueAsString(secondGenre)).toString()))
        ;
    }
    @Test
    public void whenCreateGenreShouldReturn409() throws Exception{
        GenreDto genre = new GenreDto();
        genre.setName("Фантастика");
        genre.setId(15L);
        String json = mapper.writeValueAsString(genre);
        doThrow(new ExistsException("1")).when(genreService).createGenre(any());

        this.mockMvc.perform(
                        put("/api/v1/movies/genre")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenCreateGenreShouldReturn200AndGenreReturned() throws Exception{
        GenreDto genre = new GenreDto();
        genre.setName("Фантастика");
        genre.setId(15L);
        doReturn(new Genre()).when(genreService).createGenre(any());
        doReturn(genre).when(genreMapper).toDto(any(Genre.class));
        String json = mapper.writeValueAsString(genre);

        this.mockMvc.perform(
                        put("/api/v1/movies/genre")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(genre)))
                ;



        this.mockMvc.perform(
                        get("/api/v1/movies/genre/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }
    @Test
    public void whenCreateGenresShouldReturn409() throws Exception{
        GenreDto firstGenre = new GenreDto();
        firstGenre.setName("Боевик");
        firstGenre.setId(1L);
        GenreDto secondGenre = new GenreDto();
        secondGenre.setName("Мультфильм");
        secondGenre.setId(2L);
        GenreDto thirdGenre = new GenreDto();
        thirdGenre.setName("Мультфильм");
        thirdGenre.setId(3L);
        List<GenreDto> genres = Stream.of(firstGenre, secondGenre, thirdGenre).collect(Collectors.toList());
        String json = mapper.writeValueAsString(genres);
        doReturn(Arrays.asList(new Genre(), new Genre(), new Genre())).when(genreService).createGenres(any());
        doThrow(new ExistsException(format("Genre with title '%s' is already present", thirdGenre.getName())))
                .when(genreService).createGenres(any());

        this.mockMvc.perform(
                        put("/api/v1/movies/genre/new_genres")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenCreateGenresShouldReturn200AndGenreReturned() throws Exception{
        GenreDto firstGenre = new GenreDto();
        firstGenre.setName("Боевик");
        firstGenre.setId(1L);
        GenreDto secondGenre = new GenreDto();
        secondGenre.setName("Мультфильм");
        secondGenre.setId(2L);
        GenreDto thirdGenre = new GenreDto();
        thirdGenre.setName("Фантастика");
        thirdGenre.setId(3L);
        List<GenreDto> genres = Stream.of(firstGenre, secondGenre, thirdGenre).collect(Collectors.toList());
        String json = mapper.writeValueAsString(genres);
        doReturn(Arrays.asList(new Genre(), new Genre(), new Genre())).when(genreService).createGenres(any());
        doReturn(firstGenre).doReturn(secondGenre).doReturn(thirdGenre).when(genreMapper).toDto(any(Genre.class));
        this.mockMvc.perform(
                        put("/api/v1/movies/genre/new_genres")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(List.of(mapper.writeValueAsString(firstGenre), mapper.writeValueAsString(secondGenre), mapper.writeValueAsString(thirdGenre)).toString()))
        ;



        this.mockMvc.perform(
                        get("/api/v1/movies/genre/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }
    @Test
    public void whenUpdateGenreShouldReturn409() throws Exception{
        GenreDto genre = new GenreDto();
        genre.setName("Фантастика");
        genre.setId(15L);
        String json = mapper.writeValueAsString(genre);
        doThrow(new ExistsException("1")).when(genreService).updateGenre(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/genre/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isConflict());
    }
    @Test
    public void whenUpdateGenreShouldReturn404() throws Exception{
        GenreDto genre = new GenreDto();
        genre.setName("Фантастика");
        genre.setId(15L);
        String json = mapper.writeValueAsString(genre);
        doThrow(new NotFoundException("1")).when(genreService).updateGenre(any(),anyLong());

        this.mockMvc.perform(
                        patch("/api/v1/movies/genre/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenUpdateGenreShouldReturn200AndGenreReturned() throws Exception{
        GenreDto genre = new GenreDto();
        genre.setName("Фантастика");
        genre.setId(15L);
        doReturn(new Genre()).when(genreService).updateGenre(any(),anyLong());
        doReturn(genre).when(genreMapper).toDto(any(Genre.class));
        String json = mapper.writeValueAsString(genre);

        this.mockMvc.perform(
                        patch("/api/v1/movies/genre/15")
                                .with(user("admin").roles("EDITOR"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(genre)))
        ;



        this.mockMvc.perform(
                        get("/api/v1/movies/genre/{id}",1L)
                                .with(user("admin").roles("EDITOR"))
                )
                .andExpect(status().is2xxSuccessful())
        ;
    }
    @Test
    public void whenDeletedGenreShouldReturn404() throws Exception{
        GenreDto genre = new GenreDto();
        genre.setName("Фантастика");
        genre.setId(15L);
        String json = mapper.writeValueAsString(genre);
        doThrow(new NotFoundException("1")).when(genreService).deleteGenre(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/genre/15")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    public void whenDeleteGenreShouldReturn200AndGenreReturned() throws Exception{
        GenreDto firstGenre = new GenreDto();
        firstGenre.setName("Боевик");
        firstGenre.setId(1L);
        doReturn(List.of(new Genre())).when(genreService).deleteGenre(anyLong());

        this.mockMvc.perform(
                        delete("/api/v1/movies/genre/2")
                                .with(user("admin").roles("EDITOR"))
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
        ;
    }


}