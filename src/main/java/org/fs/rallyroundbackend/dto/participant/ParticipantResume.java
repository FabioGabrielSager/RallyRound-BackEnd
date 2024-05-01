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
    protected UUID id;
    protected String name;
    @JsonProperty("base64encodedProfileImage")
    protected String profilePhoto;
}
