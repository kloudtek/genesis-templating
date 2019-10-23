package com.kloudtek.genesis.step;

import com.kloudtek.genesis.TemplateExecutionException;
import com.kloudtek.genesis.TemplateExecutor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConditionalSteps extends Step {
    private List<Step> steps;
    private String condition;
    private String value;

    public boolean isActive(TemplateExecutor exec) throws TemplateExecutionException {
        value = exec.filter(value);
        condition = exec.filter(condition);
        String varId = condition;
        String match = value;
        String resolvedValue = exec.resolveVariable(varId);
        return match != null ? resolvedValue.equals(match) : Boolean.parseBoolean(resolvedValue);
    }

    @Override
    public void execute(TemplateExecutor exec) throws TemplateExecutionException {
        if( steps != null && isActive(exec) ) {
            for (Step step : steps) {
                step.execute(exec);
            }
        }
    }


    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    @XmlAttribute(required = true)
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
