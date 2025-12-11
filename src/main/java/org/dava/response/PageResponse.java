package org.dava.response;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
  private List<T> content;
  private int page;
  private int size;
  private int totalPages;
  private long totalElements;
  private Set<String> filteredBy;
  private boolean isLastPage;
}
