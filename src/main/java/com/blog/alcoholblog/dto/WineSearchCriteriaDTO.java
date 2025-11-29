package com.blog.alcoholblog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record WineSearchCriteriaDTO(
        String name,
        String color,
        String winery,
        String kind,
        String country,
        String region,
        @Min(1900) @Max(2100)Integer year,
        @Min(0) @Max(100)Double minScore,
        @Min(0) @Max(100)Double maxScore,
        @Min(0) @Max(100)Double minAlcohol,
        @Min(0) @Max(100)Double maxAlcohol
) { }
