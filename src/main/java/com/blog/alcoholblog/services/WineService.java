package com.blog.alcoholblog.services;

import com.blog.alcoholblog.dto.CreateWineRequestDTO;
import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.exception.WineNotFoundException;
import com.blog.alcoholblog.mapper.WineMapper;
import com.blog.alcoholblog.model.Wine;
import com.blog.alcoholblog.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WineService {

    private final WineRepository wineRepository;
    private final WineMapper wineMapper;

    public WineResponseDTO getWineById(UUID id) {
        Wine wine = wineRepository.findById(id).orElseThrow(() -> new WineNotFoundException(id.toString()));

        return wineMapper.toWineResponseDTO(wine);
    }

    public List<WineResponseDTO> getAllWines() {
        return wineMapper.toWineResponseDTOList(wineRepository.findAll());
    }

    @Transactional
    public WineResponseDTO createWine(CreateWineRequestDTO createWineRequestDTO) {
        Wine wineToSave = wineMapper.toWine(createWineRequestDTO);
        Wine savedWine = wineRepository.save(wineToSave);
        return wineMapper.toWineResponseDTO(savedWine);
    }
}
