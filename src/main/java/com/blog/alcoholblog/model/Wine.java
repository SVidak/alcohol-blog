package com.blog.alcoholblog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "wines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wine {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotBlank
    @Column(name = "color", nullable = false)
    private String color;

    @NotBlank
    @Column(name = "state", nullable = false)
    private String state;

    @NotBlank
    @Column(name = "winery", nullable = false)
    private String winery;

    @NotBlank
    @Column(name = "kind", nullable = false)
    private String kind;

    @NotNull
    @Column(name = "sugar", nullable = false)
    private Double sugar;

    @NotNull
    @Column(name = "alcohol", nullable = false)
    private Double alcohol;

    @NotBlank
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank
    @Column(name = "region", nullable = false)
    private String region;

    @NotNull
    @Column(name = "score", nullable = false)
    private Double score;

    @NotBlank
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Column(name = "picture", nullable = false)
    private String picture;
}
