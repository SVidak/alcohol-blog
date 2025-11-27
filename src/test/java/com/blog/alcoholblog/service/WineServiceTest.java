package com.blog.alcoholblog.service;

import com.blog.alcoholblog.dto.*;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void testGetAllWines_WithSearchCriteria_Success() {
        // Given
        WineSearchCriteriaDTO criteria = new WineSearchCriteriaDTO(
                "Test Wine",     // name
                "Red",           // color
                "Test Winery",   // winery
                "Dry",           // kind
                "France",        // country
                "Bordeaux",      // region
                2020,            // year
                4.0,             // minScore
                5.0,             // maxScore
                12.0,            // minAlcohol
                15.0             // maxAlcohol
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Wine wine1 = new Wine();
        wine1.setId(UUID.randomUUID());
        wine1.setName("Test Wine 1");
        wine1.setColor("Red");
        wine1.setWinery("Test Winery");
        wine1.setYear(2020);
        wine1.setScore(4.5);
        wine1.setAlcohol(13.5);

        Wine wine2 = new Wine();
        wine2.setId(UUID.randomUUID());
        wine2.setName("Test Wine 2");
        wine2.setColor("Red");
        wine2.setWinery("Test Winery");
        wine2.setYear(2020);
        wine2.setScore(4.2);
        wine2.setAlcohol(14.0);

        List<Wine> filteredWines = List.of(wine1, wine2);
        Page<Wine> winePage = new PageImpl<>(filteredWines, pageable, 2);

        WineResponseDTO response1 = new WineResponseDTO(
                wine1.getId().toString(), "Test Wine 1", 2020, "Red", "Dry", "Test Winery",
                "Merlot", 1.0, 13.5, "France", "Bordeaux", 4.5, "Desc", "pic1.jpg"
        );

        WineResponseDTO response2 = new WineResponseDTO(
                wine2.getId().toString(), "Test Wine 2", 2020, "Red", "Dry", "Test Winery",
                "Cabernet", 0.75, 14.0, "France", "Bordeaux", 4.2, "Desc2", "pic2.jpg"
        );

        List<WineResponseDTO> expectedResponses = List.of(response1, response2);

        // Mock the repository to accept any Specification and return filtered results
        when(wineRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(winePage);
        when(wineMapper.toWineResponseDTOList(filteredWines)).thenReturn(expectedResponses);

        // When
        PageResponseDTO<WineResponseDTO> result = wineService.getAllWines(pageable, criteria);

        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(1, result.currentPage());
        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(10, result.size());

        // Access the content first, then the properties
        WineResponseDTO firstWine = result.content().get(0);
        WineResponseDTO secondWine = result.content().get(1);
        assertEquals("Test Wine 1", firstWine.name());
        assertEquals("Test Wine 2", secondWine.name());

        // Verify that repository was called with Specification and pageable
        verify(wineRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(wineMapper, times(1)).toWineResponseDTOList(filteredWines);
    }

    @Test
    void testGetAllWines_WithPartialSearchCriteria_Success() {
        // Given - only some criteria provided
        WineSearchCriteriaDTO criteria = new WineSearchCriteriaDTO(
                "Merlot",    // name
                null,        // color - not provided
                null,        // winery - not provided
                null,        // kind - not provided
                "France",    // country
                null,        // region - not provided
                2020,        // year
                null,        // minScore - not provided
                4.5,         // maxScore
                null,        // minAlcohol - not provided
                null         // maxAlcohol - not provided
        );

        Pageable pageable = PageRequest.of(0, 10);

        Wine wine = new Wine();
        wine.setId(UUID.randomUUID());
        wine.setName("Merlot Reserve");
        wine.setCountry("France");
        wine.setYear(2020);
        wine.setScore(4.3);

        List<Wine> filteredWines = List.of(wine);
        Page<Wine> winePage = new PageImpl<>(filteredWines, pageable, 1);

        WineResponseDTO expectedResponse = new WineResponseDTO(
                wine.getId().toString(), "Merlot Reserve", 2020, "Red", "Dry", "French Winery",
                "Merlot", 0.75, 13.0, "France", "Bordeaux", 4.3, "Description", "pic.jpg"
        );

        when(wineRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(winePage);
        when(wineMapper.toWineResponseDTOList(filteredWines)).thenReturn(List.of(expectedResponse));

        // When
        PageResponseDTO<WineResponseDTO> result = wineService.getAllWines(pageable, criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1, result.currentPage());
        assertEquals(1, result.totalPages());
        assertEquals(1, result.totalElements());
        assertEquals(10, result.size());

        // Access the content first, then the properties
        WineResponseDTO wineResponse = result.content().getFirst();
        assertEquals("Merlot Reserve", wineResponse.name());
        assertEquals("France", wineResponse.country());
        assertEquals(2020, wineResponse.year());

        verify(wineRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(wineMapper, times(1)).toWineResponseDTOList(filteredWines);
    }

    @Test
    void testGetAllWines_WithNullCriteria_ReturnsAllWines() {
        // Given - null criteria should return all wines
        Pageable pageable = PageRequest.of(0, 10);

        Wine wine1 = new Wine();
        wine1.setId(UUID.randomUUID());
        wine1.setName("Wine 1");

        Wine wine2 = new Wine();
        wine2.setId(UUID.randomUUID());
        wine2.setName("Wine 2");

        List<Wine> allWines = List.of(wine1, wine2);

        Page<Wine> winePage = new PageImpl<>(allWines, pageable, 2);

        WineResponseDTO response1 = new WineResponseDTO(
                wine1.getId().toString(), "Wine 1", 2020, "Red", "Dry", "Winery1",
                "Merlot", 1.0, 13.5, "Country1", "Region1", 4.5, "Desc1", "pic1.jpg"
        );

        WineResponseDTO response2 = new WineResponseDTO(
                wine2.getId().toString(), "Wine 2", 2019, "White", "Sweet", "Winery2",
                "Chardonnay", 0.75, 12.5, "Country2", "Region2", 4.2, "Desc2", "pic2.jpg"
        );

        List<WineResponseDTO> expectedResponses = List.of(response1, response2);

        when(wineRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(winePage);
        when(wineMapper.toWineResponseDTOList(allWines)).thenReturn(expectedResponses);

        // When - pass null criteria
        PageResponseDTO<WineResponseDTO> result = wineService.getAllWines(pageable, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(1, result.currentPage());
        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(10, result.size());

        // Verify that specification was still created (with empty criteria)
        verify(wineRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(wineMapper, times(1)).toWineResponseDTOList(allWines);
    }

    @Test
    void testGetAllWines_WithEmptyCriteria_ReturnsAllWines() {
        // Given - empty criteria (all fields null)
        WineSearchCriteriaDTO emptyCriteria = new WineSearchCriteriaDTO(
                null, null, null, null, null, null, null, null, null, null, null
        );

        Pageable pageable = PageRequest.of(0, 10);

        Wine wine1 = new Wine();
        wine1.setId(UUID.randomUUID());
        wine1.setName("Wine 1");

        Wine wine2 = new Wine();
        wine2.setId(UUID.randomUUID());
        wine2.setName("Wine 2");

        List<Wine> allWines = List.of(wine1, wine2);

        Page<Wine> winePage = new PageImpl<>(allWines, pageable, 2);

        when(wineRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(winePage);
        when(wineMapper.toWineResponseDTOList(allWines)).thenReturn(List.of(
                new WineResponseDTO(wine1.getId().toString(), "Wine 1", 2020, "Red", "Dry", "W1",
                        "Merlot", 1.0, 13.0, "C1", "R1", 4.0, "D1", "P1"),
                new WineResponseDTO(wine2.getId().toString(), "Wine 2", 2019, "White", "Sweet", "W2",
                        "Chardonnay", 0.75, 12.0, "C2", "R2", 4.2, "D2", "P2")
        ));

        // When
        PageResponseDTO<WineResponseDTO> result = wineService.getAllWines(pageable, emptyCriteria);

        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(1, result.currentPage());
        assertEquals(1, result.totalPages());
        assertEquals(2, result.totalElements());
        assertEquals(10, result.size());

        verify(wineRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(wineMapper, times(1)).toWineResponseDTOList(allWines);
    }

    @Test
    void testGetAllWines_NoMatchingResults_ReturnsEmptyList() {
        // Given - criteria that matches no wines
        WineSearchCriteriaDTO criteria = new WineSearchCriteriaDTO(
                "NonExistentWine", null, null, null, null, null, null, null, null, null, null
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Wine> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(wineRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);
        when(wineMapper.toWineResponseDTOList(List.of())).thenReturn(List.of());

        // When
        PageResponseDTO<WineResponseDTO> result = wineService.getAllWines(pageable, criteria);

        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(1, result.currentPage());
        assertEquals(0, result.totalPages());
        assertEquals(0, result.totalElements());
        assertEquals(10, result.size());

        verify(wineRepository, times(1)).findAll(any(Specification.class), eq(pageable));
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

    @Test
    void testUpdateWine_Success() {
        UUID wineId = UUID.randomUUID();

        Wine existingWine = new Wine();
        existingWine.setId(wineId);
        existingWine.setName("Old Name");

        UpdateWineRequestDTO dto = new UpdateWineRequestDTO(
                "New Name",
                null, null, null, null,
                null, null, null, null,
                null, null, null,
                null
        );

        when(wineRepository.findById(wineId))
                .thenReturn(Optional.of(existingWine));

        doAnswer(invocation -> {
            UpdateWineRequestDTO src = invocation.getArgument(0);
            Wine tgt = invocation.getArgument(1);

            if (src.name() != null) tgt.setName(src.name());

            return null;
        }).when(wineMapper).updateWineFromDTO(any(), any());

        Wine savedWine = new Wine();
        savedWine.setId(wineId);
        savedWine.setName("New Name");

        when(wineRepository.save(existingWine)).thenReturn(savedWine);

        WineResponseDTO updatedDto = new WineResponseDTO(
                wineId.toString(),
                "New Name",
                null, null, null, null, null,
                null, null, null, null, null,
                null, null
        );

        when(wineMapper.toWineResponseDTO(savedWine))
                .thenReturn(updatedDto);

        WineResponseDTO result = wineService.updateWine(wineId, dto);

        assertNotNull(result);
        assertEquals("New Name", result.name());

        verify(wineRepository).findById(wineId);
        verify(wineMapper).updateWineFromDTO(dto, existingWine);
        verify(wineRepository).save(existingWine);
        verify(wineMapper).toWineResponseDTO(savedWine);
    }



    @Test
    void testUpdateWine_NotFound() {
        UUID wineId = UUID.randomUUID();

        UpdateWineRequestDTO dto = new UpdateWineRequestDTO(
                "New name",
                null, null, null, null, null,
                null, null, null, null,
                null, null,
                null
        );

        when(wineRepository.findById(wineId)).thenReturn(Optional.empty());

        assertThrows(WineNotFoundException.class,
                () -> wineService.updateWine(wineId, dto)
        );

        verify(wineRepository).findById(wineId);
        verify(wineMapper, never()).updateWineFromDTO(any(), any());
        verify(wineRepository, never()).save(any());
    }

    @Test
    void testDeleteWine_Success() {
        UUID wineId = UUID.randomUUID();

        when(wineRepository.deleteByIdReturningCount(wineId)).thenReturn(1);

        assertDoesNotThrow(() -> wineService.deleteWineById(wineId));

        verify(wineRepository, times(1)).deleteByIdReturningCount(wineId);
    }

    @Test
    void testDeleteWine_Unsuccess() {
        UUID wineId = UUID.randomUUID();

        when(wineRepository.deleteByIdReturningCount(wineId)).thenReturn(0);

        WineNotFoundException exception =
                assertThrows(WineNotFoundException.class, () -> wineService.deleteWineById(wineId));

        assertTrue(exception.getMessage().contains(wineId.toString()));

        verify(wineRepository, times(1)).deleteByIdReturningCount(wineId);
    }
}