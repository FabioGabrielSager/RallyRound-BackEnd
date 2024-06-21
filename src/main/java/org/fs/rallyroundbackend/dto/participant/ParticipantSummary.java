package org.fs.rallyroundbackend.dto.participant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Base64;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class ParticipantSummary {
    protected UUID id;
    protected String name;
    @JsonProperty("base64encodedProfileImage")
    protected String profilePhoto;

    public ParticipantSummary(UUID participantId, String participantName, byte[] profilePhoto) {
        this.id = participantId;
        this.name = participantName;
        this.profilePhoto = profilePhoto != null ? Base64.getEncoder().encodeToString(profilePhoto) : null;
    }

}
