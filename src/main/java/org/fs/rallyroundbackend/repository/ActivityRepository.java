package org.fs.rallyroundbackend.repository;

import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    Optional<ActivityEntity> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT a FROM ActivityEntity as a WHERE a.name LIKE :name% " +
            "OR a.name LIKE %:name OR a.name LIKE %:name%")
    List<ActivityEntity> findMatchesByName(@Param("name") String name);
}
