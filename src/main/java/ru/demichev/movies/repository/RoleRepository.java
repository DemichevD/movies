package ru.demichev.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.demichev.movies.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
