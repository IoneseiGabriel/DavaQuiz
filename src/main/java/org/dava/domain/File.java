package org.dava.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "files",
    indexes = {@Index(name = "idx_file_url", columnList = "url")})
@Entity
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 10_485_760, message = "File content must not exceed 10 MB")
  @Lob
  @Column(columnDefinition = "BLOB")
  private byte[] content;

  @NotBlank
  @Length(max = 100)
  @Column(unique = true)
  private String name;

  @NotBlank
  @Length(max = 150)
  @Column(unique = true)
  private String url;

  @NotBlank
  @Length(max = 50)
  @Column(name = "content_type")
  private String contentType;
}
