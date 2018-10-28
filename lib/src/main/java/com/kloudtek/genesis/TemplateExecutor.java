package com.kloudtek.genesis;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateExecutor {
    private static final Logger logger = LoggerFactory.getLogger(TemplateExecutor.class);
    private final Configuration fmCfg;
    private Template template;
    private File target;
    private List<Input> steps;
    private List<FSObj> files;
    private final Map<String, String> variables = new HashMap<>();
    private final Map<String, String> defaults = new HashMap<>();
    private boolean nonInteractive;
    private boolean isHeadless;
    private boolean advanced;
    private boolean dryRun;

    public TemplateExecutor(Template template, File target) {
        this.template = template;
        this.target = target;
        fmCfg = new Configuration(Configuration.VERSION_2_3_28);
        fmCfg.setDefaultEncoding("UTF-8");
        fmCfg.setLogTemplateExceptions(false);
        fmCfg.setWrapUncheckedExceptions(true);
    }

    public synchronized String filter(String text) throws TemplateExecutionException {
        if (text == null) {
            return null;
        }
        try {
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("template", text);
            fmCfg.setTemplateLoader(templateLoader);
            StringWriter buf = new StringWriter();
            fmCfg.getTemplate("template").process(variables, buf);
            return buf.toString();
        } catch (TemplateException | IOException e) {
            throw new TemplateExecutionException("An error occured while processing template: " + text, e);
        }
    }

    public synchronized List<Input> executeDryRun() {
        steps = template.getInputs();
        dryRun = true;
        try {

        } finally {
            dryRun = false;
        }
        return null;
    }

    public synchronized void execute() throws TemplateExecutionException {
        steps = template.getInputs();
        files = template.getFiles();
        logger.info("Generating template to " + target);
        if (!target.exists()) {
            if (!target.mkdirs()) {
                throw new TemplateExecutionException("Unable to create directory " + target);
            }
        } else if (!target.isDirectory()) {
            throw new TemplateExecutionException("Target is not a directory " + target);
        }
        if (steps != null) {
            for (Input input : steps) {
                input.ask(this);
            }
        }
        if (files != null) {
            for (FSObj file : files) {
                file.process(this,target);
            }
            if (checkConflicts()) {
                // todo
            }
            for (FSObj file : files) {
                file.create(this,target);
            }
        }
    }



    private boolean checkConflicts() throws TemplateExecutionException {
        for (FSObj file : files) {
            if (file.isConflict()) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables.clear();
        addVariables(variables);
    }

    public String getDefaultValue(String key) {
        return defaults.get(key);

    }

    public Map<String, String> getDefaults() {
        return defaults;
    }

    public void setDefaults(Map<String, String> defaults) {
        this.defaults.clear();
        addDefault(defaults);
    }

    public void addDefault(Map<String, String> defaults) {
        this.defaults.putAll(defaults);
    }

    public void setVariable(String id, String val) {
        variables.put(id, val);
    }

    public String getVariable(String id) {
        return variables.get(id);
    }

    public void addVariables(Map<String, String> vars) {
        variables.putAll(vars);
    }

    public boolean containsVariable(String id) {
        return variables.containsKey(id);
    }

    public boolean isNonInteractive() {
        return nonInteractive;
    }

    public void setNonInteractive(boolean nonInteractive) {
        this.nonInteractive = nonInteractive;
    }

    public boolean isHeadless() {
        return isHeadless;
    }

    public void setHeadless(boolean headless) {
        isHeadless = headless;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }
}
