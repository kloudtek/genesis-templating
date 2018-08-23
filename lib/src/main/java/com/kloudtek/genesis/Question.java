package com.kloudtek.genesis;

import com.kloudtek.util.ConsoleUtils;
import com.kloudtek.util.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Map;

public class Question {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String content;
    @XmlAttribute(name = "default")
    private String defaultValue;
    private Template template;

    public void ask() throws TemplateExecutionException {
        String val = ConsoleUtils.read(content, defaultValue != null ? template.process(defaultValue) : null);
        template.setVariable(id,val);
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
