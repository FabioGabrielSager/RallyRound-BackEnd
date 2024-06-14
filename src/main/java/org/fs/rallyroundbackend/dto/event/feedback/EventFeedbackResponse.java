package org.fs.rallyroundbackend.dto.event.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventFeedbackResponse {
    private UUID feedbackId;
    private String message;
}
