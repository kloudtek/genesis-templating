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

    public void ask(Template template) {
        String val = ConsoleUtils.read(content, defaultValue != null ? StringUtils.substituteVariables(defaultValue, template.getVariables()) : null);
        template.setVariable(id,val);
    }
}
