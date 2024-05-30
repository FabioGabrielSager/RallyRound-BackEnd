package org.fs.rallyroundbackend.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    protected LocalTime selectedStartingHour;
    private UUID chatId;
}
