package org.fs.rallyroundbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class UserPrivilegeDto {
    @JsonProperty("privilegeId")
    private short id;
    @JsonProperty("privilegeName")
    private String name;
}
