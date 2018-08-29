package com.kloudtek.genesis;

import com.kloudtek.util.xml.XmlUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class Template {
    private static final Logger logger = LoggerFactory.getLogger(Template.class);
    private List<Input> steps;
    private List<FSObj> files;
    private boolean overwrite;
    private final Map<String, String> variables = new HashMap<>();
    private final Map<String, String> defaults = new HashMap<>();
    @XmlTransient
    private final Configuration fmCfg;
    private boolean nonInteractive;
    private boolean isHeadless;

    public Template() {
        fmCfg = new Configuration(Configuration.VERSION_2_3_28);
        fmCfg.setDefaultEncoding("UTF-8");
        fmCfg.setLogTemplateExceptions(false);
        fmCfg.setWrapUncheckedExceptions(true);
    }

    @XmlTransient
    public boolean isHeadless() {
        return isHeadless;
    }

    public void setHeadless(boolean headless) {
        isHeadless = headless;
    }

    @XmlTransient
    public boolean isNonInteractive() {
        return nonInteractive;
    }

    public void setNonInteractive(boolean nonInteractive) {
        this.nonInteractive = nonInteractive;
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

    public static Template create(String path) throws TemplateNotFoundException, InvalidTemplateException, IOException {
        try (InputStream is = getStream(path)) {
            if (is == null) {
                throw new TemplateNotFoundException("Template not found: " + path);
            }
            try {
                Unmarshaller unmarshaller = XmlUtils.createJAXBUnmarshaller(Template.class);
                return (Template) unmarshaller.unmarshal(is);
            } catch (JAXBException e) {
                throw new InvalidTemplateException(e);
            }
        }
    }

    private static InputStream getStream(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                //
            }
        }
        InputStream is = ClassLoader.getSystemResourceAsStream(path);
        if (is != null) {
            return is;
        }
        is = Template.class.getResourceAsStream((path.startsWith("/") ? "" : "/") + path);
        return is;
    }

    public void generate(File target) throws TemplateExecutionException {
        if (steps != null) {
            for (Input input : steps) {
                input.setTemplate(this);
            }
        }
        if (files != null) {
            for (FSObj file : files) {
                file.setTemplate(this);
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
            for (Input input : steps) {
                input.ask();
            }
        }
        if (files != null) {
            for (FSObj file : files) {
                file.process(target);
            }
            if (checkConflicts()) {
                // todo
            }
            for (FSObj file : files) {
                file.create(target);
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

    @XmlElementWrapper(name = "steps")
    @XmlElements({
            @XmlElement(name = "input", type = Input.class),
    })
    public List<Input> getSteps() {
        return steps;
    }

    public void setSteps(List<Input> steps) {
        this.steps = steps;
    }

    @XmlElementWrapper(name = "files")
    @XmlElements({
            @XmlElement(name = "file", type = TFile.class),
            @XmlElement(name = "dir", type = Directory.class)
    })
    public List<FSObj> getFiles() {
        return files;
    }

    public void setFiles(List<FSObj> files) {
        this.files = files;
    }

    @XmlTransient
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

    @XmlTransient
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

    public void addVariables(Map<String, String> vars) {
        variables.putAll(vars);
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean containsVariable(String id) {
        return variables.containsKey(id);
    }
}
