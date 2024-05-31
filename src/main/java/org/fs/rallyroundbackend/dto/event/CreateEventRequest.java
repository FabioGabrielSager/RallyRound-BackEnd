package org.fs.rallyroundbackend.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.entity.events.DurationUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEventRequest {
    @NotBlank
    private String activity;
    @NotBlank
    private String description;
    @Size(min = 1)
    @NotNull
    @JsonProperty("startingHours")
    private LocalTime[] eventSchedules;
    @NotBlank
    private String duration;
    @NotNull
    private DurationUnit durationUnit;
    @NotNull
    @PositiveOrZero
    private BigDecimal inscriptionPrice;
    @NotNull
    @FutureOrPresent
    private LocalDate date;
    @NotNull
    private AddressDto address;
    @NotNull
    @Positive
    private int participantsLimit;
    @NotNull
    private boolean eventCreatorIsParticipant;
    private LocalTime eventCreatorSelectedStartHour;
}
