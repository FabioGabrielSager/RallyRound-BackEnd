package org.fs.rallyroundbackend.exception.common;

public class RallyRoundApiException extends RuntimeException {
    public RallyRoundApiException() {
    }

    public RallyRoundApiException(String message) {
        super(message);
    }

    public RallyRoundApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public RallyRoundApiException(Throwable cause) {
        super(cause);
    }

    public RallyRoundApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
