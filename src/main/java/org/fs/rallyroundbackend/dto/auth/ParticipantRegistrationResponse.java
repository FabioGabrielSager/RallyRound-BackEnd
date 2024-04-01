package org.fs.rallyroundbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ParticipantRegistrationResponse {
    private String userId;
}
