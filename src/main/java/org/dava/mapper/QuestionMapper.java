package org.dava.mapper;

import org.dava.domain.Question;
import org.dava.dto.QuestionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "game", ignore = true) // set via Game.addQuestion(...)
  Question toEntity(QuestionRequest request);
}
