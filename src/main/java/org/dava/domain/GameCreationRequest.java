package org.dava.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameCreationRequest {

    private String title;
    private String description;

    private enum Status {DRAFT, PUBLISHED}
    private Status status;

    private String createdBy;
    private String createdAt;
    private String updatedAt;

    private int questionCount;
    private List<QuestionCreationRequest> questions;
}
