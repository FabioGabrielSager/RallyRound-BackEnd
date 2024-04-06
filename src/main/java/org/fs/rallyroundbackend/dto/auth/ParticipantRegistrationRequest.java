package org.fs.rallyroundbackend.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.dto.location.PlaceDto;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ParticipantRegistrationRequest extends RegisterRequest {
    @NotNull
    private PlaceDto place;
    @NotNull
    private ParticipantFavoriteActivityRequest[] favoritesActivities;
}
