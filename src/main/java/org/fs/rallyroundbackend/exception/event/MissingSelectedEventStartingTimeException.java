package org.fs.rallyroundbackend.exception.event;

public class MissingSelectedEventStartingTimeException extends InconsistentEventException {
    public MissingSelectedEventStartingTimeException() {
        super("No event start time has been selected.");
    }
}
