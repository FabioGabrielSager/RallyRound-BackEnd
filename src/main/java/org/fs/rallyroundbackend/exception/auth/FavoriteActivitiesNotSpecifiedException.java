package org.fs.rallyroundbackend.exception.auth;

public class FavoriteActivitiesNotSpecifiedException extends RuntimeException {
    public FavoriteActivitiesNotSpecifiedException() {
        super("Participant's favorite activities were not specified.");
    }
}
