package com.kloudtek.genesis;

import com.kloudtek.genesis.step.ConditionalSteps;
import com.kloudtek.genesis.step.Input;
import com.kloudtek.util.xml.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.List;

public class Template {
    private static final Logger logger = LoggerFactory.getLogger(Template.class);
    private String id;
    private String name;
    private String resourcePath;
    private List<Input> steps;
    private List<FSObj> files;
    private boolean overwrite;

    public Template() {
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
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

    @XmlElementWrapper(name = "steps")
    @XmlElements({
            @XmlElement(name = "input", type = Input.class),
            @XmlElement(name = "conditional", type = ConditionalSteps.class)
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

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        for (FSObj file : files) {
            file.setTemplate(this);
        }
    }
}
