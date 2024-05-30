package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.events.ScheduleVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventScheduleVoteRepository extends JpaRepository<ScheduleVoteEntity, UUID> {
}
