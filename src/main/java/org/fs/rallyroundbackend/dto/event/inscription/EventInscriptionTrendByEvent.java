package org.fs.rallyroundbackend.dto.event.inscription;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class EventInscriptionTrendByEvent {
    private String eventActivity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate eventDate;
    private EventInscriptionTrend trend;

    public EventInscriptionTrendByEvent(String eventActivity, LocalDate eventDate, Long inscriptionsCount,
                                        Long incompleteInscriptionsCount, Long canceledInscriptionCount, Long abandonmentCount) {
        this.eventActivity = eventActivity;
        this.eventDate = eventDate;
        this.trend = new EventInscriptionTrend(
                inscriptionsCount != null ? inscriptionsCount : 0,
                incompleteInscriptionsCount != null ? incompleteInscriptionsCount : 0,
                canceledInscriptionCount != null ? canceledInscriptionCount : 0,
                abandonmentCount != null ? abandonmentCount : 0
        );
    }
}
