package org.fs.rallyroundbackend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class AdminResume {
    protected UUID id;
    protected String name;
    protected String lastName;
    protected String department;
    protected LocalDateTime lastLoginTime;
    protected boolean enabled;
    private boolean isRequesterAccount;
}
