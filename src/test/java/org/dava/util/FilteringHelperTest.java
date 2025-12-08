package org.dava.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;


class FilteringHelperTest {

    private ConcurrentMap<String, Object> validFilters;
    private ConcurrentMap<String, Object> invalidFilters;
    private Set<String> validAvailableFields;

    @BeforeEach
    void setUp() {
        validFilters = new ConcurrentHashMap<>(Map.of("page", "1", "title", "Speed", "status", "DRAFT"));
        invalidFilters = new ConcurrentHashMap<>(Map.of("PAGE", "1", "title", "Speed", "status", "DRAFT"));
        validAvailableFields = getFilterFields();
    }

    @Test
    void parseFiltersWithNullOrEmptyFiltersReturnsEmptyMap() {
        Assertions.assertEquals(Collections.emptyMap(), FilteringHelper.parseFilters(null, validAvailableFields));
        Assertions.assertEquals(Collections.emptyMap(), FilteringHelper.parseFilters(new ConcurrentHashMap<>(), validAvailableFields));
    }

    @Test
    void parseFiltersWithInvalidAvailableFieldsSetThrowsIllegalArgumentException() {
        // Arrange
        Set<String> set = new HashSet<>();

        // Act & Assert
        Assertions.assertAll("Check Invalid Available Fields Assertions",
                () -> assertThrows(IllegalArgumentException.class, () -> FilteringHelper.parseFilters(validFilters, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> FilteringHelper.parseFilters(validFilters, set)));
    }

    @Test
    void parseFiltersWithPageKeyReturnsFiltersWithNoPageKey() {
        //Arrange
        Map<String, Object> expected = new HashMap<>(Map.copyOf(validFilters));
        expected.remove("page");

        // Act & Assert
        Assertions.assertEquals(expected, FilteringHelper.parseFilters(validFilters, validAvailableFields));
    }

    @Test
    void parseFiltersWithInvalidFilterKeyThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                FilteringHelper.parseFilters(invalidFilters, validAvailableFields));
    }


    private static Set<String> getFilterFields() {
        return Set.of("status", "createdAt", "updatedAt", "createdBy", "title");
    }
}