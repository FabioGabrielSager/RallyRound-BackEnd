package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.events.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, UUID> {
    Optional<ScheduleEntity> findByStartingHour(Time startingHour);
}
