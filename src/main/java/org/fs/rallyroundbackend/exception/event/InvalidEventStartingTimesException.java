package org.fs.rallyroundbackend.exception.event;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class InvalidEventStartingTimesException extends RallyRoundApiException {
    public InvalidEventStartingTimesException(String message) {
        super(message);
    }
}
