package org.fs.rallyroundbackend.exception.event.inscriptions;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class EventInscriptionAlreadyApprovedException extends RallyRoundApiException {
    public EventInscriptionAlreadyApprovedException() {
        super("This event inscription has already been approved.");
    }
}
