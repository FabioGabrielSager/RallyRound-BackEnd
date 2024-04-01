package org.fs.rallyroundbackend.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;
import java.util.UUID;

/**
 * Event published when a user sends a request to register a new participant.
 * This event is used to start the email verification process.
 * @see OnRegistrationRequestEventListener
 * */
@Getter
@Setter
public class OnRegistrationRequestEvent extends ApplicationEvent {
    private UUID user;
    private Locale locale;

    public OnRegistrationRequestEvent(UUID participantId, Locale locale) {
        super(participantId);

        this.user = participantId;
        this.locale = locale;
    }
}
