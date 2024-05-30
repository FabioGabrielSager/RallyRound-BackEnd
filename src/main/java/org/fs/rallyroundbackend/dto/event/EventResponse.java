package org.fs.rallyroundbackend.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.entity.events.DurationUnit;
import org.fs.rallyroundbackend.entity.events.EventState;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
    protected UUID id;
    protected String activity;
    protected String description;
    @JsonProperty("startingHours")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    protected LocalTime[] eventSchedules;
    protected double duration;
    protected DurationUnit durationUnit;
    protected BigDecimal inscriptionPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    protected LocalDate date;
    protected AddressDto address;
    protected int participantsLimit;
    protected boolean eventCreatorIsParticipant;
    protected ParticipantReputation eventCreatorReputation;
    protected EventState state;
    protected Map<LocalTime, Integer> startingHoursTimesVoted;
    protected List<EventParticipantDto> eventParticipants;
}
