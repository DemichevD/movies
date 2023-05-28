package ru.demichev.movies.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.repository.DirectorRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    @Transactional(readOnly=true)
    public List<Director> findAll(){
        return directorRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Director findById(Long id){
        return directorRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Director with id '%s' not found", id)));
    }

    @Transactional
    public Director createDirector(Director director){
        if(directorRepository.existsByFirstNameAndSecondNameAndLastNameAndYearOfBirth(
                director.getFirstName(),
                director.getSecondName(),
                director.getLastName(),
                director.getYearOfBirth())){
            throw new ExistsException(String.format("Director with name '%s' '%s' '%s' is already present", director.getFirstName(),director.getSecondName(), director.getLastName()));
        }
        return directorRepository.save(director);
    }

    @Transactional
    public Director updateDirector(Director director, Long id){
        Director directorToUpdate = directorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Director with id '%s' not found", id)));
        Optional<Director> existsByNameDirector = directorRepository.findDirectorByFirstNameAndSecondNameAndLastNameAndYearOfBirth(
                director.getFirstName(),
                director.getSecondName(),
                director.getLastName(),
                director.getYearOfBirth());
        if (existsByNameDirector.isPresent()){
            if(!Objects.equals(existsByNameDirector.get().getId(), id)){
                throw new ExistsException(String.format("Director with name '%s' '%s' '%s' is already present", director.getFirstName(),director.getSecondName(), director.getLastName()));
            }
        }
        directorToUpdate.setFirstName(director.getFirstName());
        directorToUpdate.setSecondName(director.getSecondName());
        directorToUpdate.setLastName(director.getLastName());
        directorToUpdate.setYearOfBirth(director.getYearOfBirth());
        directorToUpdate.setBiography(director.getBiography());
        directorToUpdate.setCountry(director.getCountry());

        return directorRepository.save(directorToUpdate);
    }

    @Transactional
    public List<Director> deleteDirector(Long id){
        Director director = directorRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Director with id '%s' not found", id)));
        directorRepository.delete(director);
        return directorRepository.findAll();
    }

}
