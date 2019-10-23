package com.kloudtek.genesis.step;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kloudtek.genesis.InvalidVariableException;
import com.kloudtek.genesis.TemplateExecutionException;
import com.kloudtek.genesis.TemplateExecutor;
import com.kloudtek.genesis.VariableMissingException;
import com.kloudtek.util.ConsoleUtils;
import com.kloudtek.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Input extends Step {
    private static final Logger logger = LoggerFactory.getLogger(Input.class);
    @JsonProperty(required = true)
    protected String var;
    @JsonProperty(required = true)
    protected String message;
    @JsonProperty()
    protected String description;
    @JsonProperty("default")
    protected String defaultValue;
    @JsonProperty
    protected boolean blankAllowed;
    @JsonProperty
    protected List<InputOption> options;
    @JsonProperty
    protected boolean advanced;

    @Override
    public void execute(TemplateExecutor exec) throws TemplateExecutionException {
        var = exec.filter(var);
        message = exec.filter(message);
        description = exec.filter(description);
        defaultValue = exec.filter(defaultValue);
        if( options != null ) {
            for (InputOption option : options) {
                option.setId(exec.filter(option.getId()));
                option.setText(exec.filter(option.getText()));
            }
        }
        updateDefaults(exec);
        if (!exec.containsVariable(var)) {
            String val = null;
            String df = defaultValue;
            String dfOverride = exec.getDefaultValue(var);
            if (StringUtils.isNotBlank(dfOverride)) {
                df = dfOverride;
            }
            if (exec.isNonInteractive() || (df != null && !advanced)) {
                if (df != null) {
                    val = df;
                } else {
                    throw new VariableMissingException("Variable " + var + " is missing", this);
                }
            } else {
                while (val == null) {
                    if (exec.isHeadless()) {
                        val = ConsoleUtils.read(message, df);
                    } else {
                        // @#$@#$@#$#@ some kind of bug breaking icon on mac os, so forcing my own icon (sigh)
                        ImageIcon icon = new ImageIcon(getClass().getResource("/com/kloudtek/genesis/questionmark.png"));
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
                        logger.debug("Input step question: "+ message);
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
            if (!checkValid(val)) {
                throw new InvalidVariableException("Invalid variable " + var + " value: " + val);
            } else {
                exec.setVariable(var, val);
            }
        } else {
            String value = exec.getVariable(var);
            if (!checkValid(value)) {
                throw new InvalidVariableException("Invalid variable " + var + " value: " + value);
            }
        }
    }

    private void updateDefaults(TemplateExecutor exec) throws TemplateExecutionException {
        if( defaultValue != null && exec.resolveVariable(var) == null ) {
            exec.getDefaults().put(var,defaultValue);
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
