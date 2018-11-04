package com.kloudtek.genesis;

import com.kloudtek.genesis.step.Input;

public class VariableMissingException extends TemplateExecutionException {
    private Input input;

    public VariableMissingException() {
    }

    public VariableMissingException(String message) {
        super(message);
    }

    public VariableMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariableMissingException(Throwable cause) {
        super(cause);
    }

    public VariableMissingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public VariableMissingException(String message, Input input) {
        super(message);
        this.input = input;
    }

    public Input getInput() {
        return input;
    }
}
