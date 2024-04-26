package org.fs.rallyroundbackend.exception.event;

public class InvalidSelectedHourException extends RuntimeException {
    public InvalidSelectedHourException() {
        super("Selected hour is not available in the provided list of hours");
    }

    public InvalidSelectedHourException(String message) {
        super(message);
    }
}
