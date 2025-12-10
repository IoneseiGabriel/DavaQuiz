package org.dava.mapper;

import static org.dava.mock.PageMockData.getPageResponse;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import org.dava.domain.Game;
import org.dava.mock.GameMockData;
import org.dava.response.PageResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

class PageMapperTest {
  private final PageMapper pageMapper = Mappers.getMapper(PageMapper.class);

  @Test
  void toResponsePageWithValidPageReturnsPageResponse() {
    // Arrange
    Page<@NotNull Game> games = new PageImpl<>(GameMockData.getValidGameList());
    PageResponse<Game> expected = getPageResponse(games, new HashSet<>());

    // Act
    PageResponse<Game> actual = pageMapper.toResponsePage(games, new HashSet<>());

    // Assert
    assertEquals(expected, actual);
  }
}
