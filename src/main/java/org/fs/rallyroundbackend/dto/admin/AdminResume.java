package org.fs.rallyroundbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd@HH:mm:ss")
    protected LocalDateTime lastLoginTime;
    protected boolean enabled;
    private boolean isRequesterAccount;
}
