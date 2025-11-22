package com.blog.alcoholblog.services;

import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.mapper.WineMapper;
import com.blog.alcoholblog.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WineService {

    private final WineRepository wineRepository;
    private final WineMapper wineMapper;

    public List<WineResponseDTO> getAllWines() {
        return wineMapper.toWineResponseDTOList(wineRepository.findAll());
    }
}
