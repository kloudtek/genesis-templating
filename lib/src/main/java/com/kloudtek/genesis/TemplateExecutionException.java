package com.kloudtek.genesis;

public class TemplateExecutionException extends Exception {
    public TemplateExecutionException() {
    }

    public TemplateExecutionException(String message) {
        super(message);
    }

    public TemplateExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateExecutionException(Throwable cause) {
        super(cause);
    }

    public TemplateExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
