package ru.demichev.movies.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@With
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "genre", schema = "movies")
public class Genre {

    @Id
    @Column(name="genre_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @ManyToMany
    private Set<Movie> movies;

    public Genre(String name){
        this.name = name;
    }

}
