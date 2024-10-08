package org.fs.rallyroundbackend.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ParticipantRegistrationRequest extends RegisterRequest {
    @Getter(value = AccessLevel.NONE)
    private boolean hasAcceptedTermsAndConditions;
    @NotNull
    private PlaceDto place;
    @NotNull
    private ParticipantFavoriteActivityDto[] favoritesActivities;

    public boolean hasAcceptedTermsAndConditions() {
        return hasAcceptedTermsAndConditions;
    }
}
