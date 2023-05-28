package ru.demichev.movies.service.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.demichev.movies.repository.GenreRepository;
import ru.demichev.movies.service.GenreService;

@TestConfiguration
public class GenreServiceConfiguration {
    @Bean
    public GenreService genreService(GenreRepository genreRepository){
        return new GenreService(genreRepository);
    }
}
