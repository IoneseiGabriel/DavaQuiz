package org.dava.dao;

import lombok.NonNull;
import org.dava.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<@NonNull File, @NonNull Long> {
    Optional<File> findByName(String name);
}