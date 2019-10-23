package com.kloudtek.genesis.step;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.kloudtek.genesis.TemplateExecutionException;
import com.kloudtek.genesis.TemplateExecutor;

import java.util.List;

@JsonTypeIdResolver(StepTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class Step {
    public abstract void execute(TemplateExecutor exec) throws TemplateExecutionException;
}
