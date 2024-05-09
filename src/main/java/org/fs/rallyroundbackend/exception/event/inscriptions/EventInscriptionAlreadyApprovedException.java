package org.fs.rallyroundbackend.exception.event.inscriptions;

public class EventInscriptionAlreadyApprovedException extends RuntimeException {
    public EventInscriptionAlreadyApprovedException() {
        super("This event inscription has already been approved.");
    }
}
