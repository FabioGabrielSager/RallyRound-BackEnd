package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.events.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, UUID> {
    Optional<ScheduleEntity> findByStartingHour(Time startingHour);
}
