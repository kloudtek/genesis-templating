package com.kloudtek.genesis.step;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.genesis.InvalidVariableException;
import com.kloudtek.genesis.TemplateExecutionException;
import com.kloudtek.genesis.TemplateExecutor;
import com.kloudtek.genesis.VariableMissingException;
import com.kloudtek.util.ConsoleUtils;
import com.kloudtek.util.StringUtils;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Input extends Step {
    @XmlAttribute(required = true)
    @JsonProperty(required = true)
    protected String id;
    @XmlAttribute(required = true)
    @JsonProperty(required = true)
    protected String name;
    @XmlAttribute()
    @JsonProperty()
    protected String description;
    @XmlAttribute(name = "default")
    @JsonProperty("default")
    protected String defaultValue;
    @XmlAttribute
    @JsonProperty
    protected boolean blankAllowed;
    @XmlElement(name = "option")
    @JsonProperty
    protected List<InputOption> options;
    @XmlAttribute
    @JsonProperty
    protected boolean advanced;
    @XmlAttribute
    @JsonProperty
    protected Question.Type type = Question.Type.STRING;

    @Override
    public List<Question> getQuestions(TemplateExecutor exec) throws TemplateExecutionException {
        updateDefaults(exec);
        return Collections.singletonList(new Question(id, exec.filter(name), exec.filter(description), exec.filter(defaultValue), blankAllowed, options, advanced, type));
    }

    public void ask(TemplateExecutor exec) throws TemplateExecutionException {
        updateDefaults(exec);
        if (!exec.containsVariable(id)) {
            String val = null;
            String df = exec.filter(defaultValue);
            String dfOverride = exec.getDefaultValue(id);
            if (StringUtils.isNotBlank(dfOverride)) {
                df = dfOverride;
            }
            if (exec.isNonInteractive() || (df != null && !advanced)) {
                if (df != null) {
                    val = df;
                } else {
                    throw new VariableMissingException("Variable " + id + " is missing", this);
                }
            } else {
                while (val == null) {
                    if (exec.isHeadless()) {
                        val = ConsoleUtils.read(name, df);
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
                                name, "Genesis Template Input Step", JOptionPane.QUESTION_MESSAGE, icon,
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
            if (!checkValid(val)) {
                throw new InvalidVariableException("Invalid variable " + id + " value: " + val);
            } else {
                exec.setVariable(id, val);
            }
        } else {
            String value = exec.getVariable(id);
            if (!checkValid(value)) {
                throw new InvalidVariableException("Invalid variable " + id + " value: " + value);
            }
        }
    }

    private void updateDefaults(TemplateExecutor exec) throws TemplateExecutionException {
        if( defaultValue != null && exec.resolveVariable(id) == null ) {
            exec.getDefaults().put(id,exec.filter(defaultValue));
        }
    }

    private boolean checkValid(String val) {
        if (options != null && !options.isEmpty()) {
            for (InputOption option : options) {
                if (option.getId().equalsIgnoreCase(val)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
