package org.dava.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private Long id;

    @NotBlank
    @Length(max = 100)
    private String name;

    @NotBlank
    @Length(max = 150)
    private String url;

    @NotBlank
    @Length(max = 50)
    private String contentType;
}