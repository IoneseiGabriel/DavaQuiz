package org.dava.service;

import lombok.NonNull;
import org.dava.dao.GameRepository;
import org.dava.domain.Game;
import org.dava.mapper.GameMapper;
import org.dava.mock.GameMockData;
import org.dava.mock.PageMockData;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private GameServiceImpl gameService;

    private final int pageNumber = 1;

    private final int pageSize = 10;

    private Pageable pageable;

    private Map<String, Object> filters;

    private List<Game> gameList;

    private List<GameResponse> gameResponseList;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(pageNumber, pageSize);
        filters = Map.of("title", "Speed", "status", "DRAFT");
        gameList = GameMockData.getValidGameList();
        gameResponseList = GameMockData.getValidGameList().stream().map(GameMockData::getGameResponse).toList();
    }

    @Test
    void getAllInvalidPageOrSizeNumberThrowsIllegalArgumentException() {
        Assertions.assertAll("Check Invalid Available Fields Assertions",
                () -> assertThrows(IllegalArgumentException.class, () -> gameService.getAll(-1, 0, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> gameService.getAll(0, -1, null)));
    }

    @Test
    void getAllWithUnreachablePageNumberThrowsNoSuchElementException() {
        // Arrange
        Map<String, Object> emptyFilters = new HashMap<>();
        when(gameRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        // Act & Assert
        Assertions.assertThrows(NoSuchElementException.class, () -> gameService.getAll(10, pageSize, emptyFilters),
                "Throws NoSuchElementException because pageNumber (10) >= totalPageNumber (0)");
    }

    @Test
    void getAllWithValidPaginationAndFiltersReturnsGamePageResponse() {
        // Arrange
        Page<@NonNull Game> gamePage = new PageImpl<>(gameList, pageable, gameList.size());

        when(gameRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(gamePage);

        PageResponse<GameResponse> expectedResponse = PageMockData.getPageResponse(
                new PageImpl<>(gameResponseList), filters.keySet());
        when(gameMapper.toResponsePage(gamePage, filters.keySet()))
                .thenReturn(expectedResponse);

        // Act
        PageResponse<GameResponse> result = gameService.getAll(pageNumber, pageSize, filters);

        // Assert
        Assertions.assertEquals(expectedResponse, result,
                "Result must be equal to a PageResponse containing a list of 3 GameResponse objects");
        verify(gameRepository, atMostOnce()).findAll(any(Specification.class), eq(pageable));
        verify(gameMapper, atMostOnce()).toResponsePage(gamePage, filters.keySet());
    }

    @Test
    void getAllWithPageNumberZeroAndEmptyGameListReturnsEmptyPage() {
        // Arrange
        Page<@NonNull Game> gamePage = new PageImpl<>(List.of());

        when(gameRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(gamePage);

        PageResponse<GameResponse> expectedResponse = PageMockData.getPageResponse(
                new PageImpl<>(List.of()), filters.keySet());
        when(gameMapper.toResponsePage(gamePage, filters.keySet()))
                .thenReturn(expectedResponse);

        // Act & Assert
        PageResponse<GameResponse> result = gameService.getAll(0, pageSize, filters);

        Assertions.assertEquals(expectedResponse, result, "The result must be a PageResponse containing an empty list");
    }
}