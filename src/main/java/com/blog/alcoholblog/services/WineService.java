package com.blog.alcoholblog.services;

import com.blog.alcoholblog.dto.CreateWineRequestDTO;
import com.blog.alcoholblog.dto.PageResponseDTO;
import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.dto.WineSearchCriteriaDTO;
import com.blog.alcoholblog.exception.WineNotFoundException;
import com.blog.alcoholblog.mapper.WineMapper;
import com.blog.alcoholblog.model.Wine;
import com.blog.alcoholblog.repository.WineRepository;
import com.blog.alcoholblog.specification.WineSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PageResponseDTO<WineResponseDTO> getAllWines(Pageable pageable, WineSearchCriteriaDTO criteriaDTO) {
        Specification<Wine> specification = WineSpecification.wineSpecification(criteriaDTO);
        Page<Wine> winePage = wineRepository.findAll(specification, pageable);

        return new PageResponseDTO<>(
                wineMapper.toWineResponseDTOList(winePage.getContent()),
                winePage.getNumber() + 1,
                winePage.getTotalPages(),
                winePage.getTotalElements(),
                winePage.getSize()
        );
    }

    @Transactional
    public WineResponseDTO createWine(CreateWineRequestDTO createWineRequestDTO) {
        Wine wineToSave = wineMapper.toWine(createWineRequestDTO);
        Wine savedWine = wineRepository.save(wineToSave);
        return wineMapper.toWineResponseDTO(savedWine);
    }

    @Transactional
    public void deleteWineById(UUID id) {
        if (!wineRepository.existsById(id)) {
            throw new WineNotFoundException("Wine with ID: " + id + " not found");
        }

        wineRepository.deleteById(id);
    }

}
