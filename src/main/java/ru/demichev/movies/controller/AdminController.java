package ru.demichev.movies.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.demichev.movies.domain.RoleEnum;
import ru.demichev.movies.domain.UserPrincipal;
import ru.demichev.movies.dto.UserDto;
import ru.demichev.movies.dto.crt.UserCreateDto;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.mapper.UserMapper;
import ru.demichev.movies.repository.RoleRepository;
import ru.demichev.movies.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@Api
public class AdminController {
    @Autowired
    private final UserService userService;
    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    @GetMapping("/user")
    @ApiOperation("Getting list all users")
    //@PreAuthorize("@roleCheckService.canAdminAndEditorCallGetInformation(authentication)")
    public ResponseEntity<List<UserDto>> findAll(){
        return  ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/user/{id}")
    @ApiOperation("Getting user by Id")
    public ResponseEntity<UserDto> findAllById(
            @PathVariable Long id){
        return  ResponseEntity.of(userService.findById(id).map(userMapper::toDto));
    }

    @PutMapping("/user")
    @ApiOperation("Create User")
    //@PreAuthorize("@roleCheckService.canAdminAndEditorCallGetInformation(authentication)")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto dto){
        log.info(dto.getLogin());
        if(dto.getRole() == null){
             dto.setRole(RoleEnum.ROLE_EDITOR.name());
        }
        UserPrincipal UserPrincipal = userService.createUser(userMapper.toEntity(dto));
        return ResponseEntity.ok(userMapper.toDto(UserPrincipal));
    }


    @PatchMapping("/user/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserCreateDto dto,
                                              @PathVariable("id") Long id){
        UserPrincipal oldUserPrincipal = userService
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with \"%s\" doesn't exist.", id)));
        oldUserPrincipal.setPassword(dto.getPassword());
        oldUserPrincipal.setNickname(dto.getNickName());
        oldUserPrincipal.getRoles().add(roleRepository.findByName(dto.getRole()));
        userService.updateUser(oldUserPrincipal);
        return ResponseEntity.ok(userMapper.toDto(oldUserPrincipal));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<List<UserDto>> deleteUser (@PathVariable Long id){
        userService.deleteUser(id);
        return findAll();
    }


}
