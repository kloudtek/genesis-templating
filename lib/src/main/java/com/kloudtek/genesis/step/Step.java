package com.kloudtek.genesis.step;

import com.kloudtek.genesis.TemplateExecutionException;
import com.kloudtek.genesis.TemplateExecutor;

import java.util.List;

public abstract class Step {
    public abstract void execute(TemplateExecutor exec) throws TemplateExecutionException;
}
