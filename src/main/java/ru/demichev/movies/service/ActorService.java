package ru.demichev.movies.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Actor;
import ru.demichev.movies.dto.ActorDto;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.repository.ActorRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActorService {
    private final ActorRepository actorRepository;

    @Transactional(readOnly=true)
    public List<Actor> findAll(){
        return actorRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Actor findById(Long id){
        return actorRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Actor with id '%s' not found", id)));
    }

    @Transactional
    public Actor createActor(Actor actor){
        if(actorRepository.existsByFirstNameAndSecondNameAndLastNameAndYearOfBirth(
                actor.getFirstName(),
                actor.getSecondName(),
                actor.getLastName(),
                actor.getYearOfBirth())){
            throw new ExistsException(String.format("Actor with name '%s' '%s' '%s' is already present", actor.getFirstName(),actor.getSecondName(), actor.getLastName()));
        }
        return actorRepository.save(actor);
    }

    @Transactional
    public Actor updateActor(Actor actor, Long id){
        Actor actorToUpdate = actorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Actor with id '%s' not found", id)));
        Optional<Actor> existsByNameActor = actorRepository.findActorByFirstNameAndSecondNameAndLastNameAndYearOfBirth(
                actor.getFirstName(),
                actor.getSecondName(),
                actor.getLastName(),
                actor.getYearOfBirth());
        if (existsByNameActor.isPresent()){
            if(!Objects.equals(existsByNameActor.get().getId(), id)){
                throw new ExistsException(String.format("Actor with name '%s' '%s' '%s' is already present", actor.getFirstName(),actor.getSecondName(), actor.getLastName()));
            }
        }
        actorToUpdate.setFirstName(actor.getFirstName());
        actorToUpdate.setSecondName(actor.getSecondName());
        actorToUpdate.setLastName(actor.getLastName());
        actorToUpdate.setYearOfBirth(actor.getYearOfBirth());
        actorToUpdate.setBiography(actor.getBiography());
        actorToUpdate.setCountry(actor.getCountry());

        return actorRepository.save(actorToUpdate);
    }

    @Transactional
    public List<Actor> deleteActor(Long id){
        Actor actor = actorRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Actor with id '%s' not found", id)));
        actorRepository.delete(actor);
        return actorRepository.findAll();
    }

    @Transactional
    public Actor getById(Long id){
        return actorRepository.getReferenceById(id);
    }

    public List<Actor> findActorsNotAssignedToMovie(Long id){
        return actorRepository.findActorNotAssignedToMovie(id);
    }

    public List<Actor> findActorsAssignedToMovie(Long id){
        return actorRepository.findActorAssignedToMovie(id);
    }

    public List<ActorDto> findActorsAssignedToMovieWithProjection(Long id){
        return actorRepository.findActorAssignedToMovieWithProjection(id);
    }
}
