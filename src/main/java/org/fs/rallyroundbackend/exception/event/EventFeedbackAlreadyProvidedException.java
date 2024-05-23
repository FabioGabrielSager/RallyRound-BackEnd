package org.fs.rallyroundbackend.exception.event;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class EventFeedbackAlreadyProvidedException extends RallyRoundApiException {

    public EventFeedbackAlreadyProvidedException() {
        super("Feedback has already been provided for this event.");
    }

    public EventFeedbackAlreadyProvidedException(String message) {
        super(message);
    }
}
