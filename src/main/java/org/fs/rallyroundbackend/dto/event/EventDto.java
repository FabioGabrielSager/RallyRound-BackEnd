package org.fs.rallyroundbackend.dto.event;

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
public class EventDto {
    private String activity;
    private String description;
    private LocalTime[] startHours;
    private String duration;
    private DurationUnit durationUnit;
    private BigDecimal inscriptionPrice;
    private LocalDate date;
    private AddressDto address;
}
