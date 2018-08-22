package com.kloudtek.genesis;

import com.kloudtek.util.xml.XmlUtils;
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
    @XmlAttribute
    private TemplateEngineType defaultType = TemplateEngineType.NONE;
    @XmlElement(name = "question")
    @XmlElementWrapper
    private List<Question> questions;
    @XmlElementWrapper(name = "files")
    @XmlElements({
            @XmlElement(name = "file", type = TFile.class),
            @XmlElement(name = "dir", type = Directory.class)
    })
    private List<TFile> files;
    private Map<String, String> variables = new HashMap<>();

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
                question.ask(this);
            }
        }
        if (files != null) {
            for (TFile file : files) {
                file.create(this,target);
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
