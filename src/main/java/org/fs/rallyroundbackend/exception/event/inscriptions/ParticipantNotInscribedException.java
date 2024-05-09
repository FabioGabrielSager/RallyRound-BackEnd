package org.fs.rallyroundbackend.exception.event.inscriptions;

public class ParticipantNotInscribedException extends RuntimeException {
    public ParticipantNotInscribedException() {
        super("The given user is not participant of the given event");
    }
}
