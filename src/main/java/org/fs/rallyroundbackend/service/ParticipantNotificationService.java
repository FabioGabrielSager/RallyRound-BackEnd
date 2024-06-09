package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.participant.ParticipantNotificationDto;
import org.fs.rallyroundbackend.dto.participant.ParticipantNotificationResponse;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

public interface ParticipantNotificationService {
    void sendNotification(@Validated ParticipantNotificationDto notification, UUID participantId);
    void sendEventInvitation(@Validated ParticipantNotificationDto notification, UUID participantId);
    List<ParticipantNotificationResponse> getNotViewedParticipantNotifications(String participantEmail);
    ParticipantNotificationResponse markNotificationAsViewed(UUID notificationId, String participantEmail);
    void removeEventsNotifications(UUID eventId, UUID participantId);
}
