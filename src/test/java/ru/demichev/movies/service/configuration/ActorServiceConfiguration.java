package ru.demichev.movies.service.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.demichev.movies.repository.ActorRepository;
import ru.demichev.movies.service.ActorService;

@TestConfiguration
public class ActorServiceConfiguration {

    @Bean
    public ActorService actorService(ActorRepository actorRepository){
        return new ActorService(actorRepository);
    }
}
