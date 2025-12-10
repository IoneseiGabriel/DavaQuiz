package org.dava.domain;

import jakarta.persistence.*;
import lombok.*;

/** JPA entity representing an application user persisted in the database. */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  /** Hashed password. */
  @Column(name = "password", nullable = false)
  private String password;
}
