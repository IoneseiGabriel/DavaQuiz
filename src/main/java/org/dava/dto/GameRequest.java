package org.dava.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dava.enumeration.GameStatus;

@Getter
@Setter
@AllArgsConstructor
public class GameRequest {

  private String title;

  private String description;

  private GameStatus status;

  private Long createdBy;
  private String createdAt;
  private String updatedAt;

  private int questionCount;
  private List<QuestionRequest> questions;
}
