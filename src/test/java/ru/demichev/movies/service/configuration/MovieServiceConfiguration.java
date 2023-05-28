package ru.demichev.movies.service.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.demichev.movies.repository.MovieRepository;
import ru.demichev.movies.service.MovieService;

@TestConfiguration
public class MovieServiceConfiguration {

    @Bean
    public MovieService movieService(MovieRepository movieRepository){
        return new MovieService(movieRepository);
    }
}
