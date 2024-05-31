package org.fs.rallyroundbackend.dto.participant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantNotificationType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ParticipantNotificationDto {
    @NotNull
    private ParticipantNotificationType type;
    @NotNull
    private UUID impliedResourceId;
    @NotBlank
    private String title;
    @NotBlank
    private String message;
    private boolean isParticipantEventCreated;
}
