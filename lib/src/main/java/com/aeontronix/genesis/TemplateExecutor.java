package com.aeontronix.genesis;

import com.aeontronix.genesis.step.Input;
import com.aeontronix.genesis.step.Step;
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
import java.util.Set;

public class TemplateExecutor {
    private static final Logger logger = LoggerFactory.getLogger(TemplateExecutor.class);
    private final Configuration fmCfg;
    private Template template;
    private List<Input> steps;
    private List<TFile> files;
    private final Map<String, String> variables = new HashMap<>();
    private final Map<String, String> defaultOverrides = new HashMap<>();
    private final Map<String, String> defaults = new HashMap<>();
    private boolean nonInteractive;
    private boolean isHeadless;
    private boolean advanced;

    public TemplateExecutor(Template template) {
        this.template = template;
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
            Map<String, Object> vars = new HashMap<>();
            vars.putAll(defaults);
            vars.putAll(defaultOverrides);
            vars.putAll(variables);
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("template", text);
            fmCfg.setTemplateLoader(templateLoader);
            StringWriter buf = new StringWriter();
            fmCfg.getTemplate("template").process(vars, buf);
            return buf.toString();
        } catch (TemplateException | IOException e) {
            throw new TemplateExecutionException("An error occured while processing template: " + text, e);
        }
    }

    public synchronized void execute(File target) throws TemplateExecutionException {
        defaults.clear();
        steps = template.getSteps();
        files = template.getFiles();

        if( template.getResourceLoader() != null) {
            // archived or directory template, let's reconciliate it's files with the files
            // listed in descriptor
            try {
                Set<String> archFiles = template.getResourceLoader().listFiles();
                for (TFile file : files) {
                    String path = file.getResource() != null ? file.getResource() : file.getPath();
                    if( path.startsWith("/") ) {
                        path = path.substring(1);
                    }
                    archFiles.remove(path);
                }
                for (String f : archFiles) {
                    template.addFile(f).setResource(f);
                }
            } catch (IOException e) {
                throw new TemplateExecutionException(e);
            }
        }

        logger.info("Generating template to " + target);
        if (!target.exists()) {
            if (!target.mkdirs()) {
                throw new TemplateExecutionException("Unable to create directory " + target);
            }
        } else if (!target.isDirectory()) {
            throw new TemplateExecutionException("Target is not a directory " + target);
        }
        if (steps != null) {
            for (Step step : steps) {
                step.execute(this);
            }
        }
        if (files != null) {
            for (TFile file : files) {
                file.process(this, target, template);
            }
            if (checkConflicts()) {
                // todo
            }
            for (TFile file : files) {
                file.create(this, target);
            }
        }
    }

    private boolean checkConflicts() {
        for (TFile file : files) {
            if (file.isConflict()) {
                return true;
            }
        }
        return false;
    }

    public String resolveVariable(String id) {
        if (variables.containsKey(id)) {
            return variables.get(id);
        } else if (defaultOverrides.containsKey(id)) {
            return defaultOverrides.get(id);
        } else {
            return defaults.get(id);
        }
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables.clear();
        addVariables(variables);
    }

    public String getDefaultValue(String key) {
        return defaultOverrides.get(key);
    }

    public Map<String, String> getDefaultOverrides() {
        return defaultOverrides;
    }

    public void setDefaultOverrides(Map<String, String> defaultOverrides) {
        this.defaultOverrides.clear();
        addDefault(defaultOverrides);
    }

    public void addDefault(Map<String, String> defaults) {
        this.defaultOverrides.putAll(defaults);
    }

    public Map<String, String> getDefaults() {
        return defaults;
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
}
