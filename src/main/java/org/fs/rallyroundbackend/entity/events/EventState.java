package org.fs.rallyroundbackend.entity.events;

public enum EventState {
    WAITING_FOR_PARTICIPANTS,
    READY_TO_START,
    SOON_TO_START,
    IN_PROCESS,
    FINALIZED,
    CANCELED
}
