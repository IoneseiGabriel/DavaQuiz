package org.dava.mapper;

import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.dava.domain.Game;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

/**
 * MapStruct mapper responsible for converting between {@link Game} entity and its corresponding
 * DTOs such as {@link GameResponse}.
 *
 * <p>This mapper is registered as a Spring bean (via {@code componentModel = "spring"}) and is
 * automatically implemented by MapStruct at build time.
 */
@Mapper(componentModel = "spring")
public interface GameMapper {

  PageMapper pageMapper = Mappers.getMapper(PageMapper.class);

  GameResponse toGameResponse(Game gameEntity);

  List<GameResponse> toGameResponseList(List<Game> gameEntityList);

  default PageResponse<GameResponse> toResponsePage(Page<@NonNull Game> page, Set<String> filters) {
    Page<@NonNull GameResponse> gameResponsePage = page.map(this::toGameResponse);

    return pageMapper.toResponsePage(gameResponsePage, filters);
  }
}
