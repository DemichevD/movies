package ru.demichev.movies.mapper.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Role;
import ru.demichev.movies.domain.UserPrincipal;
import ru.demichev.movies.dto.crt.UserCreateDto;
import ru.demichev.movies.dto.UserDto;
import ru.demichev.movies.mapper.UserMapper;
import ru.demichev.movies.repository.RoleRepository;

import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional(readOnly = true)
public class UserMapperImpl implements UserMapper {

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDto toDto(UserPrincipal source) {
        if(source == null){
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(source.getId());
        userDto.setLogin(source.getLogin());
        userDto.setNickname(source.getNickname());
        userDto.setEmail(source.getEmail());
        userDto.setRoles(source.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return userDto;
    }

    @Override
    public UserPrincipal toEntity(UserCreateDto source) {
        if(source == null){
            return null;
        }

        return new UserPrincipal(
                source.getLogin(),
                source.getNickName(),
                source.getEmail(),
                source.getPassword(),
                roleRepository.findByName(source.getRole())
        );
    }
}
