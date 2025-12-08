package org.dava.dto;

import lombok.Getter;
import lombok.Setter;
import org.dava.enumeration.GameStatus;

@Getter
@Setter
public class GameUpdateRequest {
    private String title;

    private String description;

    private GameStatus status;
}
