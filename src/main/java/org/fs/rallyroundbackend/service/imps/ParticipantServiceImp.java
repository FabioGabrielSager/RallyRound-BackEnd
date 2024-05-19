package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.participant.ReportRequest;
import org.fs.rallyroundbackend.dto.participant.ReportResponse;
import org.fs.rallyroundbackend.dto.participant.UserPublicDataDto;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;
import org.fs.rallyroundbackend.entity.users.participant.ReportEntity;
import org.fs.rallyroundbackend.exception.report.ReportsLimitException;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.fs.rallyroundbackend.service.ParticipantService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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

    @Override
    @Transactional
    public ReportResponse registerParticipantReport(ReportRequest reportRequest, String reporterEmail) {
        ParticipantEntity reportedParticipant =
                this.participantRepository.findById(reportRequest.getReportedUserId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Participant with id " + reportRequest.getReportedUserId() + " not found"));

        ParticipantEntity reporter = this.participantRepository.findEnabledUserByEmail(reporterEmail)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Participant with email " + reporterEmail + " not found"));


        if(reportedParticipant.getReports() != null) {
            List<ReportEntity> reports = reportedParticipant
                    .getReports()
                    .stream()
                    .filter(r -> r.getReporterId().equals(reporter.getId()))
                    .toList();

            if(reports.size() > 2
                    || !reports.isEmpty()
                    && reports.stream().anyMatch(r -> r.isAsEventCreator() == reportRequest.isAsEventCreator())) {
                throw new ReportsLimitException("You have already reported this user.");
            }
        } else {
            reportedParticipant.setReports(new ArrayList<>());
        }


        ReportEntity reportEntity = ReportEntity.builder()
                .motive(reportRequest.getReportMotive())
                .description(reportRequest.getDescription())
                .reportedParticipant(reportedParticipant)
                .asEventCreator(reportRequest.isAsEventCreator())
                .reporterId(reporter.getId())
                .build();

        reportedParticipant.getReports().add(reportEntity);

        int reportedParticipantReportsCount = reportedParticipant.getReports().size();

        if(reportedParticipantReportsCount > 10 && reportedParticipantReportsCount < 20) {
            if(reportRequest.isAsEventCreator()) {
                reportedParticipant.setReputationAsEventCreator(ParticipantReputation.INTERMEDIATE);
            } else {
                reportedParticipant.setReputationAsParticipant(ParticipantReputation.INTERMEDIATE);
            }
        } else if(reportedParticipantReportsCount > 20) {
            if(reportRequest.isAsEventCreator()) {
                reportedParticipant.setReputationAsEventCreator(ParticipantReputation.BAD);
            } else {
                reportedParticipant.setReputationAsParticipant(ParticipantReputation.BAD);
            }
        }

        this.participantRepository.save(reportedParticipant);

        return ReportResponse.builder()
                .reportNumber(reportedParticipantReportsCount)
                .description(reportEntity.getDescription())
                .reportMotive(reportEntity.getMotive())
                .reportedUserId(reportedParticipant.getId())
                .asEventCreator(reportRequest.isAsEventCreator())
                .build();
    }
}
