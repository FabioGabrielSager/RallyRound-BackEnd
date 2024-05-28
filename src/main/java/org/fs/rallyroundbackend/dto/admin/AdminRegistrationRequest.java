package org.fs.rallyroundbackend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.dto.auth.RegisterRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AdminRegistrationRequest extends RegisterRequest {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String department;
    @NotNull
    private List<UserPrivilegeCategoryDto> privileges;
}
