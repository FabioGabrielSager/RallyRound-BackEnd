package org.fs.rallyroundbackend.exception.auth;

/**
 * Exception thrown when the participant's favorite activities are not specified during registration.
 */
public class FavoriteActivitiesNotSpecifiedException extends RuntimeException {
    public FavoriteActivitiesNotSpecifiedException() {
        super("Participant's favorite activities were not specified.");
    }
}
