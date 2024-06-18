package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class EventsInscriptionTrendByMonthAndYear {
    private int month;
    private int year;
    private List<EventInscriptionTrendByEvent> results;
}
