package org.dava.mapper;

import java.util.Set;
import lombok.NonNull;
import org.dava.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

/**
 * MapStruct mapper responsible for converting between a {@link Page} of any type and its
 * corresponding DTO {@link PageResponse}.
 */
@Mapper(componentModel = "spring")
public interface PageMapper {
  default <T> PageResponse<T> toResponsePage(Page<@NonNull T> page, Set<String> filters) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .filteredBy(filters)
        .page(page.getNumber())
        .size(page.getSize())
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .isLastPage(page.isLast())
        .build();
  }
}
