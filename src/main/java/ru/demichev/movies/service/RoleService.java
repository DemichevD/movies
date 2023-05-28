package ru.demichev.movies.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.demichev.movies.domain.Role;
import ru.demichev.movies.repository.RoleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        Role newRole = roleRepository.save(role);
        return newRole;
    }

    public Boolean deleteRole(Long id) {
        roleRepository.deleteById(id);
        return true;
    }
}
