package org.fs.rallyroundbackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ConfirmParticipantRegistrationRequest {
    private int code;
    private String userId;
}
