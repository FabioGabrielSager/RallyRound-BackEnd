package org.fs.rallyroundbackend.exception.location;

/**
 * Exception thrown when the provided address was not found during a registration.
 */
public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException() {
        super("The provided address was not found");
    }
}
