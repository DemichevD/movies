package ru.demichev.movies.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@With
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="movie", schema = "movies")
public class Movie {

    @Id
    @Column(name="movie_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="movie_title")
    private String title;

    @Column(name="movie_year")
    private Integer year;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name="description")
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "movies")
    private Set<Actor> actors;

    @JsonIgnore
    @ManyToMany(mappedBy = "movies")
    private Set<Genre> genres;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    public Movie(String title, String description, Integer year) {
        this.title = title;
        this.description = description;
        this.year = year;
    }

    public Movie(String title, String description, Integer year, Director referenceById) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.director = referenceById;
    }

    public Movie(Long id,String title, String description, Integer year, Director referenceById) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.year = year;
        this.director = referenceById;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id) && Objects.equals(title, movie.title) && Objects.equals(year, movie.year) && Objects.equals(description, movie.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
