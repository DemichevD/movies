package ru.demichev.movies.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@With
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "director", schema = "movies")
public class Director {

    @Id
    @Column(name="director_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_Name")
    private String secondName;

    @Column(name = "last_Name")
    private String lastName;

    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name="biography")
    private String biography;

    @Column(name = "country")
    private String country;

    @JsonIgnore
    @OneToMany(mappedBy = "director", orphanRemoval = true, cascade = CascadeType.ALL, fetch = LAZY)
    private List<Movie> movies;

    public Director(String firstName, String lastName,Integer yearOfBirth){
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Director director = (Director) o;
        return id.equals(director.id) && firstName.equals(director.firstName) && Objects.equals(secondName, director.secondName) && lastName.equals(director.lastName) && Objects.equals(yearOfBirth, director.yearOfBirth) && Objects.equals(biography, director.biography) && Objects.equals(country, director.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, secondName, lastName, yearOfBirth, biography, country);
    }
}
