package ru.demichev.movies.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.dto.MovieDto;
import ru.demichev.movies.dto.crt.MovieCreateDto;
import ru.demichev.movies.exceptions.ControllerException;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.MovieMapper;
import ru.demichev.movies.service.ActorService;
import ru.demichev.movies.service.MovieService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/movies/movie")
@Api
public class MovieController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;
    private final ActorService actorService;

    @GetMapping("")
    @ApiOperation("Getting list all movies")
    public ResponseEntity<List<MovieDto>> findAll(){
        return  ResponseEntity.ok(movieService.findAll().stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Getting movie by Id")
    public ResponseEntity<MovieDto> findAllById(
            @PathVariable Long id){
        try {
            Movie movie = movieService.findById(id);
            return new ResponseEntity<>(movieMapper.toDto(movie), HttpStatus.OK);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Movie with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping("")
    @ApiOperation("Create movie")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<MovieDto> createMovie(@RequestBody @Valid MovieCreateDto dto){
        try {
            Movie movie = movieService.createMovie(movieMapper.toEntity(dto));
            return new ResponseEntity<>(movieMapper.toDto(movie), HttpStatus.CREATED);
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format("Movie with title '%s' is already present", dto.getTitle()),
                    HttpStatus.CONFLICT
            );
        }
    }


    @PatchMapping("{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<MovieDto> updateMovie(@RequestBody @Valid MovieCreateDto dto,
                                                @PathVariable("id") Long id){
        try{
            Movie movie = movieService.updateMovie(movieMapper.toEntity(dto), id);
            return new ResponseEntity<>(movieMapper.toDto(movie), HttpStatus.OK);
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format("Movie with title '%s' is already present", dto.getTitle()),
                    HttpStatus.CONFLICT
            );
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Movie with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<List<MovieDto>> deleteMovie (@PathVariable Long id){
        try {
            movieService.deleteMovie(id);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Movie with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(movieService
                .findAll()
                .stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList())
        );
    }

    @Transactional
    @PostMapping("/{movieId}/assign")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public MovieDto assignActor(@PathVariable("movieId") Long movie_id,
                             @RequestParam(name = "actor_id") Long actor_id){
        Movie movie = movieService.getById(movie_id);
        Actor actor = actorService.getById(actor_id);
        actor.getMovies().add(movie);
        movie.getActors().add(actor);
        return movieMapper.toDto(movieService.updateMovie(movie,movie_id));
    }

    @GetMapping("/movie/actor/{id}")
    public List<MovieDto> findMoviesByActor(@PathVariable Long id){
        return movieService.getMovie(id).stream().map(movieMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/movie/directors")
    public Map<String, Object> findMoviesWithDirectors(){
        return movieService.findDirectorWithMovies();
    }

}
