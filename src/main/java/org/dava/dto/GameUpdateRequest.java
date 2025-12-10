package org.dava.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.dava.enumeration.GameStatus;

@Getter
@Setter
public class GameUpdateRequest {

    @Size(min = 3, message = "Title must be at least 3 characters long")
    private String title;

    private String description;

    private GameStatus status;
}
