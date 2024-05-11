package org.fs.rallyroundbackend.exception.event;

public class MissingEventCreatorException extends InconsistentEventException {
    public MissingEventCreatorException() {
        super("Inconsistent event. It has no registered creator.");
    }
}
