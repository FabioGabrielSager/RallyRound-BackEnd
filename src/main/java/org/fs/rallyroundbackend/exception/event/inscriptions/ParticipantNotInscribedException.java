package org.fs.rallyroundbackend.exception.event.inscriptions;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class ParticipantNotInscribedException extends RallyRoundApiException {
    public ParticipantNotInscribedException() {
        super("The given user is not participant of the given event");
    }

    public ParticipantNotInscribedException(String message) {
        super(message);
    }
}
