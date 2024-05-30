package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.events.EventSchedulesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventScheduleRepository extends JpaRepository<EventSchedulesEntity, UUID> {
}
