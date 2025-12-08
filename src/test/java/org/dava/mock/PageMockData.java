package org.dava.mock;

import lombok.NonNull;
import org.dava.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.Set;

public class PageMockData {
    public static  <T> PageResponse<T> getPageResponse(Page<@NonNull T> page, Set<String> filters) {
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
