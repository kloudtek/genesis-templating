package com.kloudtek.genesis;

import com.kloudtek.util.ConsoleUtils;
import com.kloudtek.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Input extends Question implements Step {
    @NotNull
    public List<Question> getQuestions(TemplateExecutor exec) {
        return Collections.singletonList(this);
    }

    public void ask(TemplateExecutor exec) throws TemplateExecutionException {
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
