package org.dava.dao;

import jakarta.persistence.criteria.*;
import lombok.NonNull;
import org.dava.domain.Game;
import org.dava.enumeration.GameStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameSpecificationTest {

    @Mock
    private Root<Game> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Predicate predicate;

    @Mock
    Path<Object> statusPath;

    @Mock
    Path<Object> titlePath;

    @Mock
    Expression<String> lowerTitleExpression;

    @Mock
    Path<Object> createdAtPath;

    private Map<String, Object> invalidFilter;

    private Map<String, Object> validFilters;

    @BeforeEach
    void setUp() {
        validFilters = Map.of("status", "DRAFT", "updatedAt", "2024-01-10");
        invalidFilter = Map.of("invalidKey", "DRAFT");
    }

    @Test
    void createSpecificationWithNullOrEmptyFiltersReturnNull() {
        Assertions.assertAll("Check Create Specifications With Null Or Empty Filters",
                () -> assertNull(GameSpecification.createSpecification(null)),
                () -> assertNull(GameSpecification.createSpecification(new HashMap<>()))
        );
    }

    @Test
    void createSpecificationWithInvalidFiltersThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> GameSpecification.createSpecification(invalidFilter),
                "Invalid filter key: GameSpecification cannot resolve a specification for this field.");
    }

    @Test
    void isStatusWithValidStatusFilterReturnsSpecification() {
        // Arrange
        Map<String, Object> statusFilter = Map.of("status", "DRAFT");

        when(root.get("status")).thenReturn(statusPath);
        when(cb.equal(statusPath, GameStatus.DRAFT)).thenReturn(predicate);

        // Act
        Specification<@NonNull Game> spec = GameSpecification.createSpecification(statusFilter);
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        assertThat(result).isSameAs(predicate);
        verify(cb).equal(statusPath, GameStatus.DRAFT);
    }

    @Test
    void titleContainsWithValidTitleFilterReturnsSpecification() {
        // Arrange
        Map<String, Object> filters = Map.of("title", "Test");

        when(root.get("title")).thenReturn(titlePath);
        when(cb.lower(any())).thenReturn(lowerTitleExpression);
        when(cb.like(eq(lowerTitleExpression), anyString())).thenReturn(predicate);

        // Act
        Specification<@NonNull Game> spec = GameSpecification.createSpecification(filters);
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        assertThat(result).isNotNull();
        verify(cb).like(lowerTitleExpression, "%test%");
    }

    @Test
    void equalsToDateWithValidDateFilterReturnsSpecification() {
        // Arrange
        Map<String, Object> filters = Map.of("createdAt", "2024-01-10");

        when(root.get("createdAt")).thenReturn(createdAtPath);
        when(cb.between(
                any(),
                any(OffsetDateTime.class),
                any(OffsetDateTime.class)
        )).thenReturn(predicate);

        // Act
        Specification<@NonNull Game> spec = GameSpecification.createSpecification(filters);
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        Assertions.assertNotNull(result, "The result must be a predicate.");
    }

    @Test
    void createSpecificationWithMultipleFiltersReturnsSpecification() {
        // Arrange
        when(root.get("status")).thenReturn(statusPath);
        when(cb.equal(statusPath, GameStatus.DRAFT)).thenReturn(predicate);

        when(root.get("updatedAt")).thenReturn(createdAtPath);
        when(cb.between(
                any(),
                any(OffsetDateTime.class),
                any(OffsetDateTime.class)
        )).thenReturn(predicate);

        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        // Act
        Specification<@NonNull Game> spec = GameSpecification.createSpecification(validFilters);
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        Assertions.assertNotNull(result, "The result must be a predicate.");
        verify(cb, atLeastOnce()).and(any(Predicate.class), any(Predicate.class));
    }
}