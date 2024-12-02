package com.github.yufiriamazenta.craftorithm.recipe.exception;

public class RecipeLoadException extends RuntimeException {

    public RecipeLoadException() {
    }

    public RecipeLoadException(String message) {
        super(message);
    }

    public RecipeLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecipeLoadException(Throwable cause) {
        super(cause);
    }

    public RecipeLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
