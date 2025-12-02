package org.dava.service;


import org.dava.response.GameResponse;
import org.dava.response.PageResponse;

import java.util.Map;

public interface GameService {

    PageResponse<GameResponse> getAll(int page, int size, Map<String, Object> filters);
}
