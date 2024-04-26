package org.fs.rallyroundbackend.dto.participant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class ParticipantResume {
    private UUID id;
    private String name;
    @JsonProperty("base64encodedProfileImage")
    private String profilePhoto;
}
