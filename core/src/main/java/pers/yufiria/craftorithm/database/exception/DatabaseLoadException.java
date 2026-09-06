package pers.yufiria.craftorithm.database.exception;

public class DatabaseLoadException extends RuntimeException {

    public DatabaseLoadException() {
    }

    public DatabaseLoadException(String message) {
        super(message);
    }

    public DatabaseLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseLoadException(Throwable cause) {
        super(cause);
    }

    public DatabaseLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
