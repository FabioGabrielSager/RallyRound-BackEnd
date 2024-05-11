package org.fs.rallyroundbackend.exception.event;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class InconsistentEventException extends RallyRoundApiException {
    public InconsistentEventException() {
    }

    public InconsistentEventException(String message) {
        super(message);
    }

    public InconsistentEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public InconsistentEventException(Throwable cause) {
        super(cause);
    }

    public InconsistentEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
