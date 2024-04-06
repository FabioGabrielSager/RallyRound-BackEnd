package org.fs.rallyroundbackend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegisterRequest {
    @NotBlank
    protected String name;
    @NotBlank
    protected String lastName;
    @NotBlank
    @Email
    protected String email;
    @NotBlank
    protected LocalDate birthdate;
    @NotBlank
    protected String password;
}
