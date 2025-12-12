package org.dava.dao;

import java.util.Optional;
import lombok.NonNull;
import org.dava.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<@NonNull File, @NonNull Long> {
  Optional<File> findByName(String name);
}
