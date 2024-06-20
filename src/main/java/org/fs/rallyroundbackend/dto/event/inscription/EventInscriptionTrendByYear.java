package org.fs.rallyroundbackend.dto.event.inscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventInscriptionTrendByYear {
    private int year;
    private List<EventInscriptionTrendByMonth> trends;
}
