package org.dava.mapper;

import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.dava.domain.Game;
import org.dava.dto.GameRequest;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

  @Mapping(
      target = "questionCount",
      expression = "java(gameEntity.getQuestions() != null ? gameEntity.getQuestions().size() : 0)")
  @Mapping(
      target = "createdAt",
      expression =
          "java(gameEntity.getCreatedAt() != null ? gameEntity.getCreatedAt().toString() : null)")
  @Mapping(
      target = "updatedAt",
      expression =
          "java(gameEntity.getUpdatedAt() != null ? gameEntity.getUpdatedAt().toString() : null)")
  GameResponse toGameResponse(Game gameEntity);

  List<GameResponse> toGameResponseList(List<Game> gameEntityList);

  default PageResponse<GameResponse> toResponsePage(Page<@NonNull Game> page, Set<String> filters) {
    Page<@NonNull GameResponse> gameResponsePage = page.map(this::toGameResponse);

    return pageMapper.toResponsePage(gameResponsePage, filters);
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "questionCount", ignore = true)
  @Mapping(target = "questions", ignore = true)
  @Mapping(target = "createdBy", source = "userId")
  Game toEntity(GameRequest request, Long userId);
}
