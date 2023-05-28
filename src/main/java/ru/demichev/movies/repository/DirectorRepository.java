package ru.demichev.movies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Director;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    @Transactional
    Optional<Director> findDirectorByLastName(String lastName);

    Optional<Director> findDirectorByFirstNameAndSecondNameAndLastNameAndYearOfBirth(String firstName, String secondName, String LastName, int year);
    Boolean existsByFirstNameAndSecondNameAndLastNameAndYearOfBirth(String firstName, String secondName, String LastName, int year);
}
