package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.auth.ParticipantFavoriteActivityDto;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ParticipantAccountModificationRequest {
    protected String name;
    protected String lastName;
    protected LocalDate birthdate;
    private PlaceDto place;
    private ParticipantFavoriteActivityDto[] favoritesActivities;
}
