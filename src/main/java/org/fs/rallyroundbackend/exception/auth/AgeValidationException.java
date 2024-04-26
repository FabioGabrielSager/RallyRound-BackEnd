package org.fs.rallyroundbackend.exception.auth;

/**
 * Exception thrown when the age validation for a participant fails.
 */
public class AgeValidationException extends RuntimeException {
    public AgeValidationException(String message) {
        super(message);
    }
}
