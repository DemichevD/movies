package ru.demichev.movies.service;

import ru.demichev.movies.domain.UserPrincipal;
import ru.demichev.movies.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> findAll();
    Optional<UserPrincipal> findById(Long id);
    UserPrincipal createUser(UserPrincipal userPrincipal);
    UserPrincipal updateUser(UserPrincipal userPrincipal);
    Boolean deleteUser(Long id);
}
