package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventCompleteDto {
    protected UUID eventId;
    protected EventDto event;
    protected List<EventParticipantResponse> eventParticipants;
}
