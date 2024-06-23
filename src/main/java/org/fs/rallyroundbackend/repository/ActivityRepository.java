package org.fs.rallyroundbackend.repository;

import org.fs.rallyroundbackend.dto.event.EventsForActivity;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    @Query(
            "SELECT a FROM ActivityEntity a " +
                    "WHERE a.name=LOWER(:name)"
    )
    Optional<ActivityEntity> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT a FROM ActivityEntity as a WHERE (a.name LIKE :name% " +
            "OR a.name LIKE %:name OR a.name LIKE %:name%) AND a.enabled IS TRUE " +
            "ORDER BY a.name " +
            "LIMIT 10")
    List<ActivityEntity> findMatchesByName(@Param("name") String name);

    @Query(
        "SELECT a FROM ActivityEntity a WHERE " +
                "(:name IS NULL OR a.name LIKE :name% OR a.name LIKE %:name OR a.name LIKE %:name%) " +
                "AND (:enabled IS NULL OR a.enabled=:enabled)"
    )
    Page<ActivityEntity> findAll(String name, Boolean enabled, Pageable pageable);

    @Query(
            "SELECT new org.fs.rallyroundbackend.dto.event.EventsForActivity(a.name, " +
                    "COUNT(DISTINCT e), " +
                    "SUM(CASE WHEN e.state = 'FINALIZED' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN e.state = 'CANCELED' THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN e.state != 'FINALIZED' AND e.state != 'CANCELED' THEN 1 ELSE 0 END)) " +
                    "FROM ActivityEntity a " +
                    "JOIN EventEntity e ON a=e.activity " +
                    "WHERE MONTH(e.date) = :month " +
                    "   AND (:inscriptionFeeType IS NULL " +
                    "       OR :inscriptionFeeType = 'paid' AND e.inscriptionPrice > 0 " +
                    "       OR :inscriptionFeeType = 'unpaid' AND e.inscriptionPrice = 0) " +
                    "GROUP BY a.id, a.name " +
                    "ORDER BY COUNT(DISTINCT e) DESC " +
                    "LIMIT 5 "
    )
    List<EventsForActivity> getEventsForActivity(int month, String inscriptionFeeType);
}
