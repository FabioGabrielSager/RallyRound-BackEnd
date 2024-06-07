package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.participant.ParticipantNotificationDto;
import org.fs.rallyroundbackend.dto.participant.ParticipantNotificationResponse;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantNotificationEntity;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantNotificationRepository;
import org.fs.rallyroundbackend.repository.user.participant.ParticipantRepository;
import org.fs.rallyroundbackend.service.ParticipantNotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantNotificationServiceImp implements ParticipantNotificationService {
    private final ParticipantNotificationRepository notificationRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void sendNotification(ParticipantNotificationDto notification, UUID participantId) {
        ParticipantEntity participant = this.participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant with id " + participantId + " not found"));

        ParticipantNotificationEntity notificationEntity = this.modelMapper
                .map(notification, ParticipantNotificationEntity.class);

        notificationEntity.setId(null);
        notificationEntity.setTimestamp(LocalDateTime.now());
        notificationEntity.setViewed(false);
        notificationEntity.setParticipant(participant);

        if (participant.getNotifications() == null) {
            participant.setNotifications(new ArrayList<>());
        }

        participant.getNotifications().add(notificationEntity);

        this.participantRepository.save(participant);
        this.messagingTemplate
                .convertAndSendToUser(String.valueOf(participantId), "/queue/notification",
                        this.modelMapper.map(notificationEntity, ParticipantNotificationResponse.class));
    }

    @Override
    public List<ParticipantNotificationResponse> getNotViewedParticipantNotifications(String participantEmail) {
        return List.of(this.modelMapper.map(
                this.notificationRepository.findNotViewedParticipantNotifications(participantEmail),
                ParticipantNotificationResponse[].class)
        );
    }

    @Override
    @Transactional
    public ParticipantNotificationResponse markNotificationAsViewed(UUID notificationId, String participantEmail) {
        ParticipantNotificationEntity notification = this.notificationRepository
                .findParticipantNotification(notificationId, participantEmail)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        notification.setViewed(true);

        notification = this.notificationRepository.save(notification);

        return this.modelMapper.map(notification, ParticipantNotificationResponse.class);
    }

    @Override
    public void removeEventsNotifications(UUID eventId, UUID participantId) {
        this.notificationRepository.deleteAllByImpliedResourceIdAndParticipantId(eventId, participantId);
    }
}
