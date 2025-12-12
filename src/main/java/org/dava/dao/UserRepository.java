package org.dava.dao;

import java.util.Optional;
import org.dava.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for accessing and managing UserEntity data. */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsername(String username);
}
