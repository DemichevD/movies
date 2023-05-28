package ru.demichev.movies.testUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestRESTFacade {
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private Environment environment;


    public <T> ResponseEntity<T> exchange(
            String url,
            HttpMethod httpMethod,
            Object body,
            Class<T> responseType
    ) {
        return rest
                .exchange(
                "http://localhost:" + environment.getProperty("local.server.port") + url,
                httpMethod,
                new HttpEntity<>(body),
                responseType
        );
    }

    public TestRestTemplate withBasicAuth(String username, String password){
        return rest.withBasicAuth(username,password);
    }
}
