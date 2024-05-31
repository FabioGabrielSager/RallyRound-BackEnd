package org.fs.rallyroundbackend.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
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
import org.fs.rallyroundbackend.entity.events.EventState;
import org.fs.rallyroundbackend.entity.users.participant.EventInscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EventResumeDto {
    private UUID eventId;
    @NotBlank
    private String activity;
    @Size(min = 1)
    @NotNull
    @JsonProperty("startingHours")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime[] eventSchedules;
    @NotBlank
    private double duration;
    @NotNull
    private DurationUnit durationUnit;
    @PositiveOrZero
    private BigDecimal inscriptionPrice;
    @Future
    private LocalDate date;
    @NotNull
    private AddressDto address;
    @Positive
    private int participantsLimit;
    @NotNull
    private int participantsCount;
    private EventInscriptionStatus inscriptionStatus;
    private EventState state;
}
