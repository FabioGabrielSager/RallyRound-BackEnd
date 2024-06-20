package org.fs.rallyroundbackend.dto.event.inscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventInscriptionTrend {
    private long inscriptionsCount;
    private long incompleteInscriptionsCount;
    private long canceledInscriptionCount;
    private long abandonmentCount;
}
