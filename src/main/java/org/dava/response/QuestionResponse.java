package org.dava.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

  private String text;

  private List<String> options;

  private Integer correctOptionIndex;

  private String imageUrl;
}
