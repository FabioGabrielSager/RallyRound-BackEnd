package org.fs.rallyroundbackend.exception.auth;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

/**
 * Exception thrown when the participant's favorite activities are not specified during registration.
 */
public class FavoriteActivitiesNotSpecifiedException extends RallyRoundApiException {
    public FavoriteActivitiesNotSpecifiedException() {
        super("Participant's favorite activities were not specified.");
    }
}
