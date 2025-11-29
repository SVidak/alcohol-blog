package com.blog.alcoholblog.dto;


public record WineResponseDTO(
        String id,
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