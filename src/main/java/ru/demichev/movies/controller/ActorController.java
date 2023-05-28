package ru.demichev.movies.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.dto.ActorDto;
import ru.demichev.movies.dto.crt.ActorCreateDto;
import ru.demichev.movies.exceptions.ControllerException;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.ActorMapper;
import ru.demichev.movies.service.ActorService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/movies/actor")
@Api
public class ActorController {
    private final ActorService actorService;
    private final ActorMapper actorMapper;


    @GetMapping("")
    @ApiOperation("Getting list all actors")
    public ResponseEntity<List<ActorDto>> findAll() {
        return ResponseEntity.ok(actorService.findAll().stream()
                .map(actorMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Getting actor by Id")
    public ResponseEntity<ActorDto> findAllById(
            @PathVariable Long id) {
        try {
            Actor actor = actorService.findById(id);
            return new ResponseEntity<>(actorMapper.toDto(actor), HttpStatus.OK);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Actor with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping("")
    @ApiOperation("Create actor")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<ActorDto> createActor(@RequestBody @Valid ActorCreateDto dto) {
        try {
            Actor actor = actorService.createActor(actorMapper.toEntity(dto));
            return new ResponseEntity<>(actorMapper.toDto(actor), HttpStatus.CREATED);
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format("Actor with name '%s' '%s' '%s' is already present",
                            dto.getFirstName(),
                            dto.getSecondName(),
                            dto.getLastName())
                    ,
                    HttpStatus.CONFLICT
            );
        }
    }


    @PatchMapping("{id}")
    @ApiOperation("Update actor")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<ActorDto> updateActor(@RequestBody @Valid ActorCreateDto dto,
                                                @PathVariable("id") Long id) {
        try{
            Actor actor = actorService.updateActor(actorMapper.toEntity(dto), id);
            return new ResponseEntity<>(actorMapper.toDto(actor), HttpStatus.OK);
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format("Actor with name '%s' '%s' '%s' is already present",
                            dto.getFirstName(),
                            dto.getSecondName(),
                            dto.getLastName()
                    ),
                    HttpStatus.CONFLICT
            );
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Actor with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete actor")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<List<ActorDto>> deleteActor(@PathVariable Long id) {
        try {
            actorService.deleteActor(id);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Actor with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(
                actorService
                        .findAll()
                        .stream()
                        .map(actorMapper::toDto)
                        .collect(Collectors.toList())
        );
    }


    @GetMapping("/movie/{id}")
    public List<ActorDto> findActorsNotAssignedMovie(@PathVariable Long id,
                                                  @RequestParam("assigned") boolean assigned){
        if(assigned){
            return actorService
                    .findActorsAssignedToMovie(id)
                    .stream()
                    .map(actorMapper::toDto)
                    .collect(Collectors.toList());
        }else{
            return actorService
                    .findActorsNotAssignedToMovie(id)
                    .stream()
                    .map(actorMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @GetMapping("/movieTestProjection/{id}")
    public List<ActorDto> findActorsNotAssignedMovie(@PathVariable Long id){
        return actorService.findActorsAssignedToMovieWithProjection(id);
    }
}