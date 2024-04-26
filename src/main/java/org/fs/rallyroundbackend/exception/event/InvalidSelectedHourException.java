package org.fs.rallyroundbackend.exception.event;

/**
 * Exception thrown when the selected hour is not available in the provided list of hours of an event.
 */
public class InvalidSelectedHourException extends RuntimeException {
    public InvalidSelectedHourException() {
        super("Selected hour is not available in the provided list of hours");
    }

    public InvalidSelectedHourException(String message) {
        super(message);
    }
}
