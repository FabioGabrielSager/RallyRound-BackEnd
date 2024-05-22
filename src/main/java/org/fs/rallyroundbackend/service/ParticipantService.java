package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.participant.ParticipantAccountModificationRequest;
import org.fs.rallyroundbackend.dto.participant.ReportRequest;
import org.fs.rallyroundbackend.dto.participant.ReportResponse;
import org.fs.rallyroundbackend.dto.participant.UserPersonalDataDto;
import org.fs.rallyroundbackend.dto.participant.UserPublicDataDto;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Registers a report against a participant.
     *
     * @param reportRequest the report request containing details of the report
     * @param reporterEmail the email of the user reporting the participant
     * @return ReportResponse containing the details of the registered report
     */
    ReportResponse registerParticipantReport(ReportRequest reportRequest, String reporterEmail);

    /**
     * Retrieves the personal data of a participant by their email.
     *
     * @param userEmail the email of the participant
     * @return UserPersonalDataDto containing the personal data of the participant
     */
    UserPersonalDataDto getPersonalData(String userEmail);

    /**
     * Modifies the account information of a participant.
     *
     * @param userEmail the email of the participant whose account is to be modified
     * @param request the request containing the details for modifying the participant's account
     * @param profilePhoto the new profile photo to be uploaded for the participant
     * @return UserPersonalDataDto containing the updated personal data of the participant
     */
    UserPersonalDataDto modifyParticipantAccount(String userEmail, ParticipantAccountModificationRequest request,
                                  MultipartFile profilePhoto);
}
