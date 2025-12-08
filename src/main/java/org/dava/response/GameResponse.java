package org.dava.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dava.enumeration.GameStatus;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private Long id;

    @NotBlank
    @Length(max = 100)
    private String title;

    @Length(max = 500)
    private String description;

    private GameStatus status;

    @NotNull
    private Long createdBy;

    private String createdAt;

    private String updatedAt;

    private Integer questionCount;
}

