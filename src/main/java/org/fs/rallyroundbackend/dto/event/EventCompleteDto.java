package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventCompleteDto {
    private UUID eventId;
    private EventDto event;
    private EventParticipantResponse[] eventParticipants;
}
