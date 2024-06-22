package org.fs.rallyroundbackend.dto.participant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
public class ReportResponse extends ReportRequest {
    private UUID id;
    private int reportNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd@HH:mm:ss")
    private LocalDateTime reportDateTime;
}
