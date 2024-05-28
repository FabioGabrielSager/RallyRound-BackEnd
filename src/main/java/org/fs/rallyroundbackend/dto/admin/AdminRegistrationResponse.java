package org.fs.rallyroundbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@JsonPropertyOrder({"registeredAdminId", "registrationDate", "userRoles", "privileges"})
public class AdminRegistrationResponse {
    @JsonProperty("registeredAdminId")
    private UUID id;
    private List<String> userRoles;
    private List<UserPrivilegeCategoryDto> privileges;
    private LocalDateTime registrationDate;
}
