package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.location.places.PlaceDto;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPersonalDataDto extends UserPublicDataDto {
    private String name;
    private String lastName;
    private String email;
    private LocalDate birthdate;
    private PlaceDto place;
}
