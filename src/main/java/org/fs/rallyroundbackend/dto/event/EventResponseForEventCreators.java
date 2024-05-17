package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EventResponseForEventCreators extends EventResponse {
    protected LocalTime selectedStartingHour;
    private UUID chatId;
}
