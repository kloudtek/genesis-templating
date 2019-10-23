package com.aeontronix.genesis.step;

import com.aeontronix.genesis.TemplateExecutor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.aeontronix.genesis.TemplateExecutionException;

@JsonTypeIdResolver(StepTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class Step {
    public abstract void execute(TemplateExecutor exec) throws TemplateExecutionException;
}
