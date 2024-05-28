package org.fs.rallyroundbackend.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class AdminModificationRequest {
    @NotNull
    private UUID adminId;
    private String name;
    private String lastName;
    @Email
    private String email;
    private LocalDate birthdate;
    private String phoneNumber;
    private String department;
    private List<UserPrivilegeCategoryDto> privileges;
}
