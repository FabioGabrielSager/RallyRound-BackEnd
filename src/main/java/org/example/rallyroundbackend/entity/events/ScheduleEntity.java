package org.example.rallyroundbackend.entity.events;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "schedules")
@Getter @Setter
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "starting_hour", nullable = false)
    private Time startingHour;
    @Column(name = "ending_hour", nullable = false)
    private Time endingHour;

    @OneToMany(mappedBy = "schedule")
    private List<EventSchedulesEntity> eventsSchedules;
}
