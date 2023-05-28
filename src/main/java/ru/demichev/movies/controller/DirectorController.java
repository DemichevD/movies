package ru.demichev.movies.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.dto.DirectorDto;
import ru.demichev.movies.dto.crt.DirectorCreateDto;
import ru.demichev.movies.exceptions.ControllerException;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.DirectorMapper;
import ru.demichev.movies.service.DirectorService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/movies/director")
@Api
public class DirectorController {
    private final DirectorService directorService;
    private final DirectorMapper directorMapper;

    @GetMapping("")
    @ApiOperation("Getting list all directors")
    public ResponseEntity<List<DirectorDto>> findAll(){
        return  ResponseEntity.ok(directorService.findAll().stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Getting director by Id")
    public ResponseEntity<DirectorDto> findAllById(
            @PathVariable Long id){
        try {
            Director director = directorService.findById(id);
            return new ResponseEntity<>(directorMapper.toDto(director), HttpStatus.OK);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Director with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PutMapping("")
    @ApiOperation("Create director")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<DirectorDto> createDirector(@RequestBody @Valid DirectorCreateDto dto){
        try {
            Director director = directorService.createDirector(directorMapper.toEntity(dto));
            return new ResponseEntity<>(directorMapper.toDto(director), HttpStatus.CREATED);
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format("Director with name '%s' '%s' '%s' is already present",
                            dto.getFirstName(),
                            dto.getSecondName(),
                            dto.getLastName()),
                    HttpStatus.CONFLICT
            );
        }
    }


    @PatchMapping("{id}")
    @ApiOperation("Update director")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<DirectorDto> updateDirector(@RequestBody @Valid DirectorCreateDto dto,
                                                @PathVariable("id") Long id){
        try{
            Director director = directorService.updateDirector(directorMapper.toEntity(dto), id);
            return new ResponseEntity<>(directorMapper.toDto(director), HttpStatus.OK);
        }catch (ExistsException e){
            throw new ControllerException(
                    String.format("Director with name '%s' '%s' '%s' is already present",
                            dto.getFirstName(),
                            dto.getSecondName(),
                            dto.getLastName()),
                    HttpStatus.CONFLICT
            );
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Director with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete director")
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    public ResponseEntity<List<DirectorDto>> deleteDirector (@PathVariable Long id){
        try {
            directorService.deleteDirector(id);
        }catch (NotFoundException e){
            throw new ControllerException(
                    String.format("Director with id '%s' not found", id),
                    HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(directorService
                .findAll().stream()
                .map(directorMapper::toDto)
                .collect(Collectors.toList()));
    }
}
