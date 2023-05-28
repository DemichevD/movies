package ru.demichev.movies.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Genre;
import ru.demichev.movies.dto.GenreDto;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.repository.GenreRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;


    @Transactional(readOnly=true)
    public List<Genre> findAll(){
        return genreRepository.findAllByOrderByName();
    }


    @Transactional(readOnly=true)
    public Genre findById(Long id){
        return genreRepository
                 .findById(id)
                 .orElseThrow(()-> new NotFoundException(format("Genre with id '%s' not found", id)));
    }

    @Transactional(readOnly=true)
    public Genre findByName(String name){
        return genreRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(()->new NotFoundException(format("Genre with name '%s' not found", name)));
    }

    @Transactional(readOnly=true)
    public List<Genre> findByContainingPrefix(String prefix){
        return genreRepository
                .findAllByNameContainingIgnoreCase(prefix);
    }
    
    @Transactional
    public Genre createGenre(Genre genre){
        if(genreRepository.existsByNameIgnoreCase(genre.getName())){
            throw new ExistsException(format("Genre with title '%s' is already present", genre.getName()));
        }
        return genreRepository.save(genre);
    }

    @Transactional
    public List<Genre> createGenres(List<Genre> genres){
        return genres
                .stream()
                .map(this::createGenre)
                .collect(Collectors.toList());
    }

    @Transactional
    public Genre updateGenre(Genre genre, Long id){
        Genre genreToUpdate = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Genre with \"%s\" doesn't exist.", id)));
        Optional<Genre> existsByNameGenre = genreRepository.findByNameIgnoreCase(genre.getName());
        if (existsByNameGenre.isPresent()){
            if(!Objects.equals(existsByNameGenre.get().getId(), id)){
                throw new ExistsException(format("Genre with title '%s' is already present", genre.getName()));
            }
        }
        genreToUpdate.setName(genre.getName());
        return genreRepository.save(genreToUpdate);
    }

    @Transactional
    public List<Genre> deleteGenre(Long id){
        Genre genre = genreRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(format("Genre with id '%s' not found", id)));
        genreRepository.delete(genre);
        return genreRepository.findAll();
    }

    public List<GenreDto> findGenreWithMovie(Collection<Long> genreIds, int topMoviesCount){
        return genreRepository.findGenreWithMovie(genreIds, topMoviesCount);
    }
}
