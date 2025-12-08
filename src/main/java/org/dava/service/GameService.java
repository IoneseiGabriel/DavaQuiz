package org.dava.service;

import java.util.Map;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;

public interface GameService {

  PageResponse<GameResponse> getAll(int page, int size, Map<String, Object> filters);
}
