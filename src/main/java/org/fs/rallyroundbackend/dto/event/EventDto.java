package org.fs.rallyroundbackend.dto.event;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
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
    @PositiveOrZero
    private BigDecimal inscriptionPrice;
    @Future
    private LocalDate date;
    @NotNull
    private AddressDto address;
    @Positive
    private int participantsLimit;
    @NotNull
    private boolean eventCreatorIsParticipant;
    private LocalTime eventCreatorSelectedStartHour;
    private EventState state;
    // TODO: The following value (chatId) shouldn't be included in all event details request,
    //  it should only be included in requests coming from the participants of the requested event.
    private UUID chatId;
}
