package ru.demichev.movies.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Director;
import ru.demichev.movies.domain.Movie;
import ru.demichev.movies.exceptions.ExistsException;
import ru.demichev.movies.exceptions.NotFoundException;
import ru.demichev.movies.repository.MovieRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    @Transactional(readOnly=true)
    public List<Movie> findAll(){
        return movieRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Movie findById(Long id){
        return movieRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Movie with id '%s' not found", id)));
    }
    @Transactional
    public Movie createMovie(Movie movie){
        if(movieRepository.existsByTitleAndYear(
                movie.getTitle(),
                movie.getYear())){
            throw new ExistsException(String.format("Movie with title '%s' and year '%s' is already present",movie.getTitle(),movie.getYear()));
        }
        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(Movie movie, Long id){
        Movie movieToUpdate = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Movie with id '%s' not found", id)));
        Optional<Movie> existsByNameTitleAndYear = movieRepository.findByTitleAndYear(
                movie.getTitle(),
                movie.getYear());
        if (existsByNameTitleAndYear.isPresent()){
            if(!Objects.equals(existsByNameTitleAndYear.get().getId(), id)){
                throw new ExistsException(String.format("Movie with title '%s' and year '%s' is already present", movie.getTitle(),movie.getYear()));
            }
        }
        movieToUpdate.setTitle(movie.getTitle());
        movieToUpdate.setDescription(movie.getDescription());
        movieToUpdate.setYear(movie.getYear());

        return movieRepository.save(movieToUpdate);
    }

    @Transactional
    public List<Movie> deleteMovie(Long id){
        Movie movie = movieRepository
                .findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("Movie with id '%s' not found", id)));
        movieRepository.delete(movie);
        return movieRepository.findAll();
    }

    @Transactional
    public Movie getById(Long id){
        return movieRepository.getReferenceById(id);
    }

    public List<Movie> getMovie(Long id){
        return movieRepository.findMoviesByActorId(id);
    }

    @Transactional
    public Map<String, Object> findDirectorWithMovies(){
        List<Movie> movies = movieRepository.findAllMovieWithDirector();
        Map<String, Object> cell = new HashMap<>();
        for (Movie movie: movies){
            Director director = movie.getDirector();
            cell.put(movie.getTitle(), director.getFirstName() + " " + director.getLastName());
        }
        return cell;
    }
}
