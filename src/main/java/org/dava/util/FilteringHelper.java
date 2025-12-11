package org.dava.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FilteringHelper {
  private final List<String> basicFilters = List.of("page", "size");

  public Map<String, Object> parseFilters(
      ConcurrentMap<String, Object> filters, Set<String> availableFields) {
    if (filters == null || filters.isEmpty()) {
      return Collections.emptyMap();
    }

    if (availableFields == null || availableFields.isEmpty()) {
      throw new IllegalArgumentException(
          "The list of available fields for the object cannot be empty or null");
    }
    basicFilters.forEach(filters::remove);

    for (String key : filters.keySet()) {
      if (!availableFields.contains(key)) {
        throw new IllegalArgumentException(
            String.format("No field was found with the specified name '%s'", key));
      }
    }

    return filters;
  }
}
