package com.blog.alcoholblog.service;

import com.blog.alcoholblog.dto.CreateWineRequestDTO;
import com.blog.alcoholblog.dto.WineResponseDTO;
import com.blog.alcoholblog.exception.WineNotFoundException;
import com.blog.alcoholblog.mapper.WineMapper;
import com.blog.alcoholblog.model.Wine;
import com.blog.alcoholblog.repository.WineRepository;
import com.blog.alcoholblog.services.WineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WineServiceTest {

    @Mock
    private WineRepository wineRepository;

    @Mock
    private WineMapper wineMapper;

    @InjectMocks
    private WineService wineService;

    @Test
    void testGetWineById_Success() {
        UUID wineId = UUID.randomUUID();
        Wine wine = new Wine();
        wine.setId(wineId);
        wine.setName("Test Wine");

        WineResponseDTO expectedResponse = new WineResponseDTO(
                wineId.toString(), "Test Wine", 2020, "Red", "Dry", "Winery",
                "Merlot", 1.0, 13.5, "Serbia", "Vojvodina", 4.5, "Desc", "pic.jpg"
        );

        when(wineRepository.findById(wineId)).thenReturn(Optional.of(wine));
        when(wineMapper.toWineResponseDTO(wine)).thenReturn(expectedResponse);

        WineResponseDTO result = wineService.getWineById(wineId);

        assertNotNull(result);
        assertEquals(wineId.toString(), result.id());
        assertEquals("Test Wine", result.name());

        verify(wineRepository, times(1)).findById(wineId);
        verify(wineMapper, times(1)).toWineResponseDTO(wine);
    }

    @Test
    void testGetWineById_NotFound() {
        UUID wineId = UUID.randomUUID();
        when(wineRepository.findById(wineId)).thenReturn(Optional.empty());

        WineNotFoundException exception = assertThrows(WineNotFoundException.class,
                () -> wineService.getWineById(wineId));

        assertEquals(wineId.toString(), exception.getMessage());
        verify(wineRepository, times(1)).findById(wineId);
        verify(wineMapper, never()).toWineResponseDTO(any(Wine.class));
    }

    @Test
    void testGetAllWines_Success() {
        Wine wine1 = new Wine();
        wine1.setId(UUID.randomUUID());
        wine1.setName("Wine 1");

        Wine wine2 = new Wine();
        wine2.setId(UUID.randomUUID());
        wine2.setName("Wine 2");

        List<Wine> wines = List.of(wine1, wine2);

        WineResponseDTO response1 = new WineResponseDTO(
                wine1.getId().toString(), "Wine 1", 2020, "Red", "Dry", "Winery",
                "Merlot", 1.0, 13.5, "Serbia", "Vojvodina", 4.5, "Desc", "pic.jpg"
        );

        WineResponseDTO response2 = new WineResponseDTO(
                wine2.getId().toString(), "Wine 2", 2019, "White", "Sweet", "Winery2",
                "Chardonnay", 0.75, 12.5, "France", "Bordeaux", 4.2, "Desc2", "pic2.jpg"
        );

        List<WineResponseDTO> expectedResponses = List.of(response1, response2);

        when(wineRepository.findAll()).thenReturn(wines);
        when(wineMapper.toWineResponseDTOList(wines)).thenReturn(expectedResponses);

        List<WineResponseDTO> result = wineService.getAllWines();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Wine 1", result.get(0).name());
        assertEquals("Wine 2", result.get(1).name());

        verify(wineRepository, times(1)).findAll();
        verify(wineMapper, times(1)).toWineResponseDTOList(wines);
    }

    @Test
    void testGetAllWines_Empty() {
        when(wineRepository.findAll()).thenReturn(List.of());
        when(wineMapper.toWineResponseDTOList(List.of())).thenReturn(List.of());

        List<WineResponseDTO> result = wineService.getAllWines();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(wineRepository, times(1)).findAll();
        verify(wineMapper, times(1)).toWineResponseDTOList(List.of());
    }

    @Test
    void testCreateWine_Success() {
        CreateWineRequestDTO requestDTO = new CreateWineRequestDTO(
                "New Wine", 2021, "Rose", "Semi-Dry", "New Winery", "Pinot Noir",
                0.75, 12.0, "Italy", "Tuscany", 4.3, "New description", "new_pic.jpg"
        );

        Wine wineToSave = new Wine();
        wineToSave.setName("New Wine");

        Wine savedWine = new Wine();
        UUID savedId = UUID.randomUUID();
        savedWine.setId(savedId);
        savedWine.setName("New Wine");

        WineResponseDTO expectedResponse = new WineResponseDTO(
                savedId.toString(), "New Wine", 2021, "Rose", "Semi-Dry", "New Winery",
                "Pinot Noir", 0.75, 12.0, "Italy", "Tuscany", 4.3, "New description", "new_pic.jpg"
        );

        when(wineMapper.toWine(requestDTO)).thenReturn(wineToSave);
        when(wineRepository.save(wineToSave)).thenReturn(savedWine);
        when(wineMapper.toWineResponseDTO(savedWine)).thenReturn(expectedResponse);

        WineResponseDTO result = wineService.createWine(requestDTO);

        assertNotNull(result);
        assertEquals(savedId.toString(), result.id());
        assertEquals("New Wine", result.name());

        verify(wineMapper, times(1)).toWine(requestDTO);
        verify(wineRepository, times(1)).save(wineToSave);
        verify(wineMapper, times(1)).toWineResponseDTO(savedWine);
    }
}