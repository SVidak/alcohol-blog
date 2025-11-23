package com.blog.alcoholblog.controller;

import com.blog.alcoholblog.dto.CreateWineRequestDTO;
import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.model.Wine;
import com.blog.alcoholblog.services.WineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wines")
@RequiredArgsConstructor
public class WineController {

    private final WineService wineService;

    @GetMapping("/{id}")
    public ResponseEntity<WineResponseDTO> getWineById(@Valid @PathVariable String id) {
        WineResponseDTO wine = wineService.getWineById(UUID.fromString(id));

        return ResponseEntity.ok(wine);
    }

    @GetMapping
    public ResponseEntity<List<WineResponseDTO>> getAllWines() {
        return ResponseEntity.ok(wineService.getAllWines());
    }

    @PostMapping
    public ResponseEntity<WineResponseDTO> createWine(@Valid @RequestBody CreateWineRequestDTO createWineRequestDTO) {
        WineResponseDTO createdWine = wineService.createWine(createWineRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdWine.id())
                .toUri();

        return ResponseEntity.created(location).body(createdWine);
    }
}
