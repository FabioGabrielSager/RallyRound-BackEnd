package org.fs.rallyroundbackend.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.entity.events.DurationUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EventModificationRequest {
    @NotNull
    private UUID eventId;
    private String activity;
    private String description;
    @Size(min = 1)
    @JsonProperty("startingHours")
    private LocalTime[] eventSchedules;
    private String duration;
    private DurationUnit durationUnit;
    @PositiveOrZero
    private BigDecimal inscriptionPrice;
    @Future
    private LocalDate date;
    private AddressDto address;
    @Positive
    private Integer participantsLimit;
    private Boolean eventCreatorIsParticipant;
    private LocalTime eventCreatorSelectedStartHour;
}
