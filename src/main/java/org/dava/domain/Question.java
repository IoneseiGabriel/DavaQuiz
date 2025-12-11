package org.dava.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  @JsonIgnore
  private Game game;

  @Column(name = "text", nullable = false)
  private String text;

  @ElementCollection
  @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "id"))
  private List<String> options = new ArrayList<>();

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "correct_option_index")
  private Integer correctOptionIndex;
}
