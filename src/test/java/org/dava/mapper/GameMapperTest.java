package org.dava.mapper;

import org.dava.domain.Game;
import org.dava.mock.GameMockData;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dava.mock.GameMockData.getGameResponse;
import static org.dava.mock.GameMockData.getValidGameList;
import static org.dava.mock.PageMockData.getPageResponse;

@ExtendWith(MockitoExtension.class)
class GameMapperTest {
    private final GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

    private List<Game> validGameList;

    @BeforeEach
    void setUp() {
        validGameList = getValidGameList();
    }

    @Test
    void toGameResponseWithValidGameReturnsGameResponse() {
        // Arrange
        GameResponse expected = getGameResponse(validGameList.get(0));

        // Act
        GameResponse actual = gameMapper.toGameResponse(validGameList.get(0));

        // Assert
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void toGameResponseListWithValidGameListReturnsGameResponseList() {
        // Arrange
        List<GameResponse> expected = validGameList.stream().map(GameMockData::getGameResponse).toList();

        // Act
        List<GameResponse> actual = gameMapper.toGameResponseList(validGameList);

        // Assert
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void toGameResponseWithNullGameReturnsNull() {
        // Act
        GameResponse actual = gameMapper.toGameResponse(null);

        // Assert
        Assertions.assertNull(actual);
    }

    @Test
    void toGameResponseListWithNullListReturnsNull() {
        // Act
        List<GameResponse> actual = gameMapper.toGameResponseList(null);

        // Assert
        Assertions.assertNull(actual);
    }

    @Test
    void toGameResponseWithNullCreatedAtAndNullUpdatedAtReturnsGameResponse() {
        // Arrange
        Game game = validGameList.get(0);
        game.setCreatedAt(null);
        game.setUpdatedAt(null);

        // Act
        GameResponse actual = gameMapper.toGameResponse(validGameList.get(0));

        // Assert
        assertThat(actual).usingRecursiveComparison()
                .comparingOnlyFields("createdAt", "updatedAt")
                .isEqualTo(game);
    }

    @Test
    void toResponsePageWithValidPageAndFiltersReturnsGamePageResponse() {
        // Arrange
        Page<@NotNull Game> gamePage = new PageImpl<>(validGameList);

        List<GameResponse> gameResponseList = validGameList.stream().map(GameMockData::getGameResponse).toList();
        Page<@NotNull GameResponse> gameResponsePage = new PageImpl<>(gameResponseList);

        PageResponse<GameResponse> expected = getPageResponse(gameResponsePage, new HashSet<>());

        // Act
        PageResponse<GameResponse> actual = gameMapper.toResponsePage(gamePage, new HashSet<>());

        // Assert
        assertThat(actual.getContent()).usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expected.getContent());
    }
}