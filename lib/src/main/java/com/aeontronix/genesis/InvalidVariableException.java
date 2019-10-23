package com.aeontronix.genesis;

public class InvalidVariableException extends TemplateExecutionException {
    public InvalidVariableException() {
    }

    public InvalidVariableException(String message) {
        super(message);
    }

    public InvalidVariableException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVariableException(Throwable cause) {
        super(cause);
    }

    public InvalidVariableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
