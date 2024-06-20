package org.fs.rallyroundbackend.dto.event.inscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventInscriptionTrendByMonth {
    private int month;
    private EventInscriptionTrend trends;
}
