package org.fs.rallyroundbackend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@JsonPropertyOrder({ "categoryId", "categoryName", "privileges" })
public class UserPrivilegeCategoryDto {
    @JsonProperty("categoryId")
    private short id;
    @JsonProperty("categoryName")
    private String name;
    private List<UserPrivilegeDto> privileges;
}
