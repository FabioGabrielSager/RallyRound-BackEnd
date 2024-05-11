package org.fs.rallyroundbackend.exception.event.inscriptions;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class EventStateException extends RallyRoundApiException {
    public EventStateException() {
        super("The action cannot be completed due to the current state of the event.");
    }

    public EventStateException(String message) {
        super(message);
    }
}
