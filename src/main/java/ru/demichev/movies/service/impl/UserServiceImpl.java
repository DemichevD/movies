package ru.demichev.movies.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.demichev.movies.domain.UserPrincipal;
import ru.demichev.movies.dto.UserDto;
import ru.demichev.movies.mapper.UserMapper;
import ru.demichev.movies.repository.UserRepository;
import ru.demichev.movies.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<UserPrincipal> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserPrincipal createUser(UserPrincipal userPrincipal) {
        return userRepository.save(userPrincipal);
    }

    @Override
    public UserPrincipal updateUser(UserPrincipal userPrincipal) {
        return userRepository.save(userPrincipal);
    }

    @Override
    public Boolean deleteUser(Long id) {
        userRepository.deleteById(id);
        return true;
    }
}
