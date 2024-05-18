package org.fs.rallyroundbackend.dto.auth;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParticipantFavoriteActivityDto {
    @NotBlank
    private String name;
    @NotNull
    @Min(0)
    private int order;
}
