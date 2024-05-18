package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.participant.UserPublicDataDto;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.service.ParticipantService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

/**
 * Implementation of ${@link ParticipantService} to manage participants.
 */
@Service
@RequiredArgsConstructor
public class ParticipantServiceImp implements ParticipantService {
    private final ModelMapper modelMapper;
    private final ParticipantRepository participantRepository;

    @Override
    public UserPublicDataDto getParticipantPublicData(UUID userId) {
        ParticipantEntity participantEntity =
                this.participantRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("Participant with id " + userId + " not found"));

        // TODO: Add logic to check if the requested logic was deleted.
        UserPublicDataDto result = this.modelMapper.map(participantEntity, UserPublicDataDto.class);

        result.setDeletedAccount(false);

        if (participantEntity.getProfilePhoto() != null) {
            String participantEncodedProfilePhoto = Base64.getEncoder().encodeToString(participantEntity.getProfilePhoto());
            result.setProfilePhoto(participantEncodedProfilePhoto);
        }

        return result;
    }
}
