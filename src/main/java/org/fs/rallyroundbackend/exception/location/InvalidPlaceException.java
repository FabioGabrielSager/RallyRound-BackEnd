package org.fs.rallyroundbackend.exception.location;

/**
 * Exception thrown when the provided place was not found during a registration.
 */
public class InvalidPlaceException extends RuntimeException {
    public InvalidPlaceException() {
        super("The provided place was not found");
    }
}
