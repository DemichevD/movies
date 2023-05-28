package ru.demichev.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.demichev.movies.domain.UserPrincipal;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserPrincipal, Long> {

    Optional<UserPrincipal> findUserByLogin(String login);

}
