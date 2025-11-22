package com.blog.alcoholblog.services;

import com.blog.alcoholblog.model.Wine;
import com.blog.alcoholblog.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WineService {

    private final WineRepository wineRepository;

    public List<Wine> getAllWines() {
        return wineRepository.findAll();
    }
}
