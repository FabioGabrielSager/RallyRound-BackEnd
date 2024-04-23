package org.fs.rallyroundbackend.repository.event;

import org.fs.rallyroundbackend.entity.events.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
