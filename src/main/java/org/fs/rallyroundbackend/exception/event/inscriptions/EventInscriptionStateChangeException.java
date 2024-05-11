package org.fs.rallyroundbackend.exception.event.inscriptions;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class EventInscriptionStateChangeException extends RallyRoundApiException {
    public EventInscriptionStateChangeException(String message) {
        super(message);
    }
}
