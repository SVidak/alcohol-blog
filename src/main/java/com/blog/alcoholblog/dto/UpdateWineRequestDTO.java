package com.blog.alcoholblog.dto;


public record UpdateWineRequestDTO(
        String name,
        Integer year,
        String color,
        String state,
        String winery,
        String kind,
        Double sugar,
        Double alcohol,
        String country,
        String region,
        Double score,
        String description,
        String picture
) { }
