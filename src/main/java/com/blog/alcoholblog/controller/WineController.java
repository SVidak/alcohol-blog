package com.blog.alcoholblog.controller;

import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.services.WineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WineController {

    private final WineService wineService;

    @GetMapping("/wines")
    public ResponseEntity<List<WineResponseDTO>> getAllWines() {
        return ResponseEntity.ok(wineService.getAllWines());
    }

}
