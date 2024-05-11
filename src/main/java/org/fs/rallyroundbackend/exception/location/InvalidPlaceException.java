package org.fs.rallyroundbackend.exception.location;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

/**
 * Exception thrown when the provided place was not found during a registration.
 */
public class InvalidPlaceException extends RallyRoundApiException {
    public InvalidPlaceException() {
        super("The provided place was not found");
    }
}
