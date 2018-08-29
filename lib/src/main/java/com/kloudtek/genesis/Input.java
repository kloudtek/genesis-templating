package com.kloudtek.genesis;

import com.kloudtek.util.ConsoleUtils;
import com.kloudtek.util.StringUtils;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Input {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String message;
    @XmlAttribute(name = "default")
    private String defaultValue;
    @XmlAttribute
    private boolean blankAllowed;
    @XmlElement(name = "option")
    private List<InputOption> options;
    private Template template;

    public void ask() throws TemplateExecutionException {
        if (!template.containsVariable(id)) {
            String val = null;
            String df = template.filter(defaultValue);
            String dfOverride = template.getDefaultValue(id);
            if (StringUtils.isNotBlank(dfOverride)) {
                df = dfOverride;
            }
            if (template.isNonInteractive()) {
                if (df != null) {
                    val = df;
                } else {
                    throw new TemplateExecutionException("Variable " + id + " must be set since (nonInteractive mode activated)");
                }
            } else {
                while (val == null) {
                    if (template.isHeadless()) {
                        val = ConsoleUtils.read(message, df);
                    } else {
                        // @#$@#$@#$#@ some kind of bug breaking icon on mac os, so forcing my own icon (sigh)
                        ImageIcon icon = new ImageIcon(getClass().getResource("questionmark.png"));
                        Object defaultValue = df;
                        Object[] dialogOptions = null;
                        if (options != null && !options.isEmpty()) {
                            defaultValue = null;
                            for (InputOption option : options) {
                                if (option.getId().equals(df)) {
                                    defaultValue = option;
                                    break;
                                }
                            }
                            if (blankAllowed) {
                                List<Object> list = new ArrayList<>();
                                list.add("");
                                list.addAll(options);
                                dialogOptions = list.toArray();
                            } else {
                                dialogOptions = options.toArray();
                            }
                        }
                        String response = JOptionPane.showInputDialog(null,
                                message, "Genesis Template Input Step", JOptionPane.QUESTION_MESSAGE, icon,
                                dialogOptions, defaultValue).toString();
                        if (response == null) {
                            throw new TemplateExecutionException("Template processing cancelled by user");
                        } else if (!blankAllowed && StringUtils.isBlank(response)) {
                            val = null;
                        } else {
                            val = response;
                        }
                    }
                }
            }
            template.setVariable(id, val);
        }
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
