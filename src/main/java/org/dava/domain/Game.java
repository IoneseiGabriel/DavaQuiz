package org.dava.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.dava.enumeration.GameStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "game",
    indexes = {@Index(name = "idx_game_created_by", columnList = "created_by")})
@Entity
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Length(max = 100)
  private String title;

  @Length(max = 500)
  private String description;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private GameStatus status = GameStatus.DRAFT;

  @NotNull
  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_at")
  @CreationTimestamp(source = SourceType.DB)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp(source = SourceType.DB)
  private LocalDateTime updatedAt;

  @Transient private Integer questionCount;

  @JsonProperty("questionCount")
  public Integer getQuestionCount() {
    return questions != null ? questions.size() : 0;
  }

  @Builder.Default
  @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Question> questions = new ArrayList<>();

  public void addQuestion(Question q) {
    questions.add(q);
    q.setGame(this);
  }
}
