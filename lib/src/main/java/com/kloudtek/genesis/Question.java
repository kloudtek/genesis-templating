package com.kloudtek.genesis;

import com.kloudtek.util.ConsoleUtils;

import javax.xml.bind.annotation.XmlAttribute;

public class Question {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String content;
    @XmlAttribute(name = "default")
    private String defaultValue;
    private Template template;

    public void ask() throws TemplateExecutionException {
        String val = ConsoleUtils.read(content, template.process(defaultValue) );
        template.setVariable(id, val);
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
