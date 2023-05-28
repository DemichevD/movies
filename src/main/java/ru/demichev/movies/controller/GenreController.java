package ru.demichev.movies.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.dto.GenreDto;
import ru.demichev.movies.dto.crt.GenreCreateDto;
import ru.demichev.movies.exceptions.ControllerException;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.GenreMapper;
import ru.demichev.movies.service.GenreService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/movies/genre")
@Api
public class GenreController {
    private final GenreService genreService;
    private final GenreMapper genreMapper;

    @GetMapping("")
    @ApiOperation("Getting list all genres")
    public ResponseEntity<List<GenreDto>> findAll() {
        return new ResponseEntity<>(
                genreService
                        .findAll()
                        .stream()
                        .map(genreMapper::toDto)
                        .collect(Collectors.toList()), HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @ApiOperation("Getting genre by Id")
    public ResponseEntity<GenreDto> findById(
            @Valid @NotBlank @PathVariable Long id) {
        try {
            Genre genre = genreService.findById(id);
            return new ResponseEntity<>(genreMapper.toDto(genre), HttpStatus.OK);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Genre with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/name")
    @ApiOperation("Getting genre by Name")
    public ResponseEntity<GenreDto> findByName(
            @Valid @NotBlank @RequestParam(name = "name") String name) {
        try {
            Genre genre = genreService.findByName(name);
            return new ResponseEntity<>(genreMapper.toDto(genre), HttpStatus.OK);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Genre with name '%s' not found", name),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/names")
    @ApiOperation("Getting genre by Prefix")
    public ResponseEntity<List<GenreDto>> findByPrefix(
            @Valid @NotBlank @RequestParam(name = "prefix") String prefix) {
        return new ResponseEntity<>(
                genreService.findByContainingPrefix(prefix)
                        .stream()
                        .map(genreMapper::toDto)
                        .collect(Collectors.toList()),
                HttpStatus.OK
        );
    }

    @PutMapping("")
    @ApiOperation("Create genre")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<GenreDto> createGenre(@RequestBody @Valid GenreCreateDto dto) {
        try {
            Genre genre = genreService.createGenre(genreMapper.toEntity(dto));
            return new ResponseEntity<>(genreMapper.toDto(genre), HttpStatus.CREATED);
        }catch (ExistsException e){
            throw new ControllerException(
              String.format("Genre with name '%s' is already present", dto.getName()),
              HttpStatus.CONFLICT
            );
        }
    }

    @PutMapping("/new_genres")
    @ApiOperation("Create genres")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<List<GenreDto>> createGenres(@RequestBody @Valid List<GenreCreateDto> dto) {

        try {
            List<Genre> newGenres = new ArrayList<>();
            for (GenreCreateDto genre: dto) {
                newGenres.add(genreMapper.toEntity(genre));
            }
            return new ResponseEntity<>(genreService.createGenres(newGenres)
                    .stream()
                    .map(genreMapper::toDto)
                    .collect(Collectors.toList())
                    , HttpStatus.CREATED
            );
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format(e.getMessage()),
                    HttpStatus.CONFLICT
            );
        }
    }

    @PatchMapping("/{id}")
    @ApiOperation("Update genre")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<GenreDto> updateGenre(@RequestBody @Valid GenreCreateDto dto,
                                                @PathVariable("id") Long id) {
        try{
            Genre genre = genreService.updateGenre(genreMapper.toEntity(dto), id);
            return new ResponseEntity<>(genreMapper.toDto(genre), HttpStatus.OK);
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format("Genre with name '%s' is already present", dto.getName()),
                    HttpStatus.CONFLICT
            );
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Genre with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete genre")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<List<GenreDto>> deleteGenre(@PathVariable Long id) {
        try {
            genreService.deleteGenre(id);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Genre with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(
                genreService
                        .findAll()
                        .stream()
                        .map(genreMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/top")
    @ApiOperation("Getting genre by Id in top")
    public ResponseEntity<List<GenreDto>> findAllByIdAndTop(@RequestParam(name = "genre_id") String id,
                                            @RequestParam(name = "count") String topMovieCount ) {
        List<Long> collectionId = Arrays
                .stream(id.split(","))
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(genreService.findGenreWithMovie(collectionId, Integer.parseInt(topMovieCount)));
    }

}
