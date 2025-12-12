package org.dava.dao;

import org.dava.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing Player entities. Extends JpaRepository, which
 * provides standard CRUD operations, pagination support, and JPA query method capabilities.
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {}
