package ru.demichev.movies.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.demichev.movies.domain.RoleEnum;
import ru.demichev.movies.domain.UserPrincipal;
import ru.demichev.movies.dto.crt.UserCreateDto;
import ru.demichev.movies.dto.UserDto;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.UserMapper;
import ru.demichev.movies.service.UserService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Api
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PutMapping("")
    @ApiOperation("Create User")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto dto){
        dto.setRole(RoleEnum.ROLE_VIEWER.name());
        UserPrincipal UserPrincipal = userService.createUser(userMapper.toEntity(dto));
        return ResponseEntity.ok(userMapper.toDto(UserPrincipal));
    }


    @PatchMapping("{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserCreateDto dto,
                                                      @PathVariable("id") Long id){
        UserPrincipal oldUserPrincipal = userService
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with \"%s\" doesn't exist.", id)));
        oldUserPrincipal.setPassword(dto.getPassword());
        userService.updateUser(oldUserPrincipal);
        return ResponseEntity.ok(userMapper.toDto(oldUserPrincipal));
    }

    @DeleteMapping("/{id}")
    public Boolean deleteUser (@PathVariable Long id){
        return userService.deleteUser(id);
    }
    
}
