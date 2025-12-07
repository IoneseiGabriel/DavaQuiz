package org.dava.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionCreationRequest {

    private String text;

    private List<String> options;

    private Integer correctOptionIndex;

    private String imageUrl;
}
