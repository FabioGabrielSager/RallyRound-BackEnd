package org.fs.rallyroundbackend.exception.location;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

/**
 * Exception thrown when the provided address was not found during a registration.
 */
public class InvalidAddressException extends RallyRoundApiException {
    public InvalidAddressException() {
        super("The provided address was not found");
    }
}
