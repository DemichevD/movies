package ru.demichev.movies.testUtil;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@ContextConfiguration(initializers = IntegrationSuite.Initializer.class)
public class IntegrationSuite {
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:9.6.8");

    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Startables.deepStart(POSTGRES).join();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            environment.getPropertySources().addFirst(new MapPropertySource(
                    "testcontainers",
                    Map.of(
                            "spring.datasource.url", POSTGRES.getJdbcUrl(),
                            "spring.datasource.username", POSTGRES.getUsername(),
                            "spring.datasource.password",POSTGRES.getPassword()
                    )
            ));
        }
    }
}
