package com.github.yufiriamazenta.craftorithm.exception;

public class UnsupportedVersionException extends RuntimeException {

    public UnsupportedVersionException() {
    }

    public UnsupportedVersionException(String message) {
        super(message);
    }

    public UnsupportedVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedVersionException(Throwable cause) {
        super(cause);
    }

    public UnsupportedVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
