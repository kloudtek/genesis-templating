package com.kloudtek.genesis.step;

import com.kloudtek.genesis.TemplateExecutionException;
import com.kloudtek.genesis.TemplateExecutor;

import javax.xml.bind.annotation.XmlAttribute;

public class InputOption extends Step {
    private String id;
    private String text;

    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(required = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public void execute(TemplateExecutor exec) throws TemplateExecutionException {
    }
}
