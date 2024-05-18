package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.auth.ParticipantFavoriteActivityDto;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantReputation;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UserPublicDataDto extends ParticipantResume {
    private ParticipantReputation reputationAsEventCreator;
    private ParticipantReputation reputationAsParticipant;
    private List<ParticipantFavoriteActivityDto> favoriteActivities;
    private boolean isDeletedAccount;
}
