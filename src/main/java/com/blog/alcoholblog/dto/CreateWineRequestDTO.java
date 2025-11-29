package com.blog.alcoholblog.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWineRequestDTO(
        @NotBlank String name,
        @NotNull Integer year,
        @NotBlank String color,
        @NotBlank String state,
        @NotBlank String winery,
        @NotBlank String kind,
        @NotNull Double sugar,
        @NotNull Double alcohol,
        @NotBlank String country,
        @NotBlank String region,
        @NotNull Double score,
        @NotBlank String description,
        @NotBlank String picture
) { }
