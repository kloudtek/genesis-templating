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
        if( ! template.containsVariable(id) ) {
            String df = template.filter(defaultValue);
            if( template.isNonInteractive() ) {
                if(df !=null) {
                    template.setVariable(id, df);
                } else {
                    throw new TemplateExecutionException("Variable "+id+ " must be set since (nonInteractive mode activated)");
                }
            } else {
                String val = ConsoleUtils.read(content, df);
                template.setVariable(id, val);
            }
        }
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
