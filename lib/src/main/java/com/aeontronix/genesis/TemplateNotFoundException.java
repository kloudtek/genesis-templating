package com.aeontronix.genesis;

public class TemplateNotFoundException extends Exception {
    public TemplateNotFoundException() {
    }

    public TemplateNotFoundException(String message) {
        super(message);
    }

    public TemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateNotFoundException(Throwable cause) {
        super(cause);
    }

    public TemplateNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
