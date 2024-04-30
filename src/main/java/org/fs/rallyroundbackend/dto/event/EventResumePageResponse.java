package org.fs.rallyroundbackend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EventResumePageResponse {
    private int page;
    private int pageSize;
    private long totalElements;
    private EventResumeDto[] results;
}
