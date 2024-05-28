package org.fs.rallyroundbackend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminCompleteDataDto extends AdminResume {
    private String email;
    private LocalDateTime registrationDate;
    private List<UserPrivilegeCategoryDto> privileges;
}
