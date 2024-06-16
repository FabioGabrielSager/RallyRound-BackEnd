package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventsForActivity {
    private String activity;
    private long totalEventsCount;
    private long finalizedEventsCount;
    private long canceledEventsCount;
    private long activeEventsCount;
}
