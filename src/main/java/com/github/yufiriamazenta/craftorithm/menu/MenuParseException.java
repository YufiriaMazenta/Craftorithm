package com.github.yufiriamazenta.craftorithm.menu;

public class MenuParseException extends RuntimeException {

    public MenuParseException() {
    }

    public MenuParseException(String message) {
        super(message);
    }

    public MenuParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MenuParseException(Throwable cause) {
        super(cause);
    }

    public MenuParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
