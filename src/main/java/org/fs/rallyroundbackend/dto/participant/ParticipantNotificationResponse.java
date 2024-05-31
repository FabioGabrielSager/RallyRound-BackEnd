package org.fs.rallyroundbackend.dto.participant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ParticipantNotificationResponse extends ParticipantNotificationDto {
    private UUID id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd@HH:mm:ss")
    private LocalDateTime timestamp;
    private boolean viewed;
}
