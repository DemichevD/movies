package ru.demichev.movies.service.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.demichev.movies.repository.DirectorRepository;
import ru.demichev.movies.service.DirectorService;

@TestConfiguration
public class DirectorServiceConfiguration {

    @Bean
    public DirectorService directorService(DirectorRepository directorRepository){
        return new DirectorService(directorRepository);
    }
}
