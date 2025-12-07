package org.dava.dao;

import org.dava.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

//import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {


}