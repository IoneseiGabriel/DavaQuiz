package org.dava.dao;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import lombok.NonNull;
import org.dava.domain.Game;
import org.dava.enumeration.GameStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification class that provides reusable {@link Specification} instances for filtering {@link
 * Game} entities.
 *
 * <p>Each private method in this class returns a {@code Specification<Game>} that can be combined
 * using {@link Specification#and(Specification)} to build dynamic query predicates at runtime.
 *
 * <p>This class is not intended to be instantiated and should be used by the service layer when
 * constructing filtered queries.
 */
public class GameSpecification {
  private GameSpecification() {}

  /**
   * Based on the {@code filters} provided, a {@code Specification<Game>} query predicate is
   * created. If {@code filters} contain an invalid field name, an explicit {@link
   * IllegalArgumentException} is thrown.
   *
   * @param filters a map containing {@code field=value} pairs
   * @return a {@link Specification} of {@link Game} type, build dynamically at runtime
   */
  public static Specification<@NonNull Game> createSpecification(Map<String, Object> filters) {
    if (filters == null || filters.isEmpty()) {
      return null;
    }

    Map<String, Object> currentFilters = new HashMap<>(Map.copyOf(filters));
    currentFilters.remove(null);

    Specification<@NonNull Game> gameSpec = (root, query, cb) -> null;

    for (Map.Entry<String, Object> entry : currentFilters.entrySet()) {
      gameSpec = gameSpec.and(getSpecificationByKey(entry.getKey(), entry.getValue()));
    }

    return gameSpec;
  }

  private static Specification<@NonNull Game> isStatus(String status) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("status"), GameStatus.valueOf(status.toUpperCase()));
  }

  private static Specification<@NonNull Game> titleContains(String title) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  private static Specification<@NonNull Game> equalsToDate(String dateTime, String field) {
    LocalDate date = LocalDate.parse(dateTime);

    ZoneOffset offset = ZoneOffset.UTC;

    OffsetDateTime start = date.atStartOfDay().atOffset(offset);
    OffsetDateTime end = date.plusDays(1).atStartOfDay().atOffset(offset);

    return (root, query, cb) -> cb.between(root.get(field), start, end);
  }

  private static Specification<@NonNull Game> getSpecificationByKey(String key, Object value) {
    return switch (key) {
      case "status" -> GameSpecification.isStatus((String) value);
      case "title" -> GameSpecification.titleContains((String) value);
      case "createdAt" -> GameSpecification.equalsToDate((String) value, "createdAt");
      case "updatedAt" -> GameSpecification.equalsToDate((String) value, "updatedAt");
      default ->
          throw new IllegalArgumentException(
              String.format("No field was found with the specified name '%s'", key));
    };
  }
}
