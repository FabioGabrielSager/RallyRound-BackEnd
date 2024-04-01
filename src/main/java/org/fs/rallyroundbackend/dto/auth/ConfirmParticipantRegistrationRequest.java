package org.fs.rallyroundbackend.dto.auth;

import lombok.Getter;

@Getter
public class ConfirmParticipantRegistrationRequest {
    private int code;
    private String userId;
}
