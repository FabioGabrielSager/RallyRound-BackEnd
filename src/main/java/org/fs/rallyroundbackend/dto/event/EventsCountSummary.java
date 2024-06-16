package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EventsCountSummary {
    private long totalEventsCount;
    private long finalizedEventsCount;
    private long canceledEventsCount;
    private long activeEventsCount;

    public EventsCountSummary(Long totalEventsCount, Long finalizedEventsCount, Long canceledEventsCount, Long activeEventsCount) {
        this.totalEventsCount = 0;
        this.finalizedEventsCount = 0;
        this.canceledEventsCount = 0;
        this.activeEventsCount = 0;

        if(totalEventsCount != null) {
            this.totalEventsCount = totalEventsCount;
        }

        if(finalizedEventsCount != null) {
            this.finalizedEventsCount = finalizedEventsCount;
        }

        if(canceledEventsCount != null) {
            this.canceledEventsCount = canceledEventsCount;
        }

        if(activeEventsCount != null) {
            this.activeEventsCount = activeEventsCount;
        }
    }
}
