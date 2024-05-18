package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.participant.UserPublicDataDto;

import java.util.UUID;

/**
 * Service interface for managing participants.
 */
public interface ParticipantService {

    /**
     * Retrieves the public data of a participant by their ID.
     *
     * @param userId the ID of the participant
     * @return UserPublicDataDto containing the public data of the participant
     */
    UserPublicDataDto getParticipantPublicData(UUID userId);
}
