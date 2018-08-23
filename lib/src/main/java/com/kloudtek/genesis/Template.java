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
    @XmlElement(name = "question")
    @XmlElementWrapper
    private List<Question> questions;
    @XmlElementWrapper(name = "files")
    @XmlElements({
            @XmlElement(name = "file", type = TFile.class),
            @XmlElement(name = "dir", type = Directory.class)
    })
    private List<FSObj> files;
    private Map<String, String> variables = new HashMap<>();
    private final Configuration fmCfg;

    public Template() {
        fmCfg = new Configuration(Configuration.VERSION_2_3_28);
        fmCfg.setDefaultEncoding("UTF-8");
        fmCfg.setLogTemplateExceptions(false);
        fmCfg.setWrapUncheckedExceptions(true);
    }

    public synchronized String process(String text) throws TemplateExecutionException {
        try {
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("template", text);
            fmCfg.setTemplateLoader(templateLoader);
            StringWriter buf = new StringWriter();
            fmCfg.getTemplate("template").process(variables, buf);
            return buf.toString();
        } catch (TemplateException | IOException e) {
            throw new TemplateExecutionException(e);
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
        for (Question question : questions) {
            question.setTemplate(this);
        }
        for (FSObj file : files) {
            file.setTemplate(this);
        }
        logger.info("Generating template to " + target);
        if (!target.exists()) {
            if (!target.mkdirs()) {
                throw new TemplateExecutionException("Unable to create directory " + target);
            }
        } else if (!target.isDirectory()) {
            throw new TemplateExecutionException("Target is not a directory " + target);
        }
        if (questions != null) {
            for (Question question : questions) {
                question.ask();
            }
        }
        if (files != null) {
            for (FSObj file : files) {
                file.create(target);
            }
        }
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariable(String id, String val) {
        variables.put(id, val);
    }
}
