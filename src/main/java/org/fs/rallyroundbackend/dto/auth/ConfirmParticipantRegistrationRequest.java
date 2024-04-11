package org.fs.rallyroundbackend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ConfirmParticipantRegistrationRequest {
    @NotNull
    @Min(100000)
    @Max(999999)
    private int code;
    @Email
    @NotBlank
    private String email;
}
