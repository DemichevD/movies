package ru.demichev.movies.mapper;

import ru.demichev.movies.domain.UserPrincipal;
import ru.demichev.movies.dto.crt.UserCreateDto;
import ru.demichev.movies.dto.UserDto;

public interface UserMapper {
    UserDto toDto(UserPrincipal source);
    UserPrincipal toEntity(UserCreateDto source);
}
