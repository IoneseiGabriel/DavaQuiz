package org.dava.validator;

import org.dava.dto.CreatePlayerRequest;

public interface PlayerValidator {
  void validateRequest(CreatePlayerRequest playerRequest);
}
