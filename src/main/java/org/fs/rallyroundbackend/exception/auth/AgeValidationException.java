package org.fs.rallyroundbackend.exception.auth;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

/**
 * Exception thrown when the age validation for a participant fails.
 */
public class AgeValidationException extends RallyRoundApiException {
    public AgeValidationException(String message) {
        super(message);
    }
}
