package org.dava.dao;

import lombok.NonNull;
import org.dava.domain.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<@NonNull Game, @NonNull Long> {
    Page<@NonNull Game> findAll(Specification<@NonNull Game> gameSpec, Pageable pageable);
}
