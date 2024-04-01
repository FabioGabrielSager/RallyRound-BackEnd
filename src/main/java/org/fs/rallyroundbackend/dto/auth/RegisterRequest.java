package org.fs.rallyroundbackend.dto.auth;

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
    protected String name;
    protected String lastName;
    protected String email;
    protected LocalDate birthdate;
    protected String password;
}
