package com.kloudtek.genesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.genesis.step.ConditionalSteps;
import com.kloudtek.genesis.step.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@XmlRootElement
public class Template {
    private static final Logger logger = LoggerFactory.getLogger(Template.class);
    private String id;
    private String name;
    private String resourcePath;
    private List<Input> steps;
    private List<FSObj> files;
    private boolean overwrite;
    private ResourceLoader resourceLoader;

    public Template() {
    }

    public Template(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(required = true)
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
        Template template;
        ObjectMapper objectMapper = new ObjectMapper();
        String lpath = path.toLowerCase();
        try {
            URL url = new URL(path);
            if(lpath.endsWith(".json")) {
                return loadJson(url);
            } else if( lpath.endsWith(".jar") || lpath.endsWith(".zip") ) {

            }
        } catch (MalformedURLException e) {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    return loadTemplate(new DirectoryResourceLoader(file));
                } else {
                    if (lpath.endsWith(".json")) {
                        return loadJson(file);
                    } else if( lpath.endsWith(".jar") || lpath.endsWith(".zip") ) {
                        return loadTemplate(new ZipResourceLoader(file));
                    }
                }
            } else {
                throw new TemplateNotFoundException("Template "+path +" not found");
            }
        }
        return null;
    }

    private static Template loadTemplate(ResourceLoader resourceLoader) throws IOException, InvalidTemplateException {
        try (InputStream is = resourceLoader.loadResource("genesis-template.json'")) {
            if (is == null) {
                throw new InvalidTemplateException("Unable to find template file in archive");
            }
            Template template = loadJson(is);
            template.setResourceLoader(resourceLoader);
            return template;
        }
    }

    private static Template loadJson(InputStream is) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(is,Template.class);
    }

    private static Template loadJson(URL url) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(url,Template.class);
    }

    private static Template loadJson(File file) throws InvalidTemplateException {
        try {
            try( FileInputStream is = new FileInputStream(file) ){
                return loadJson(is);
            }
        } catch (IOException e) {
            throw new InvalidTemplateException(e);
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

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public InputStream loadResource(String resourcePath) {
        if (resourceLoader != null) {
            return resourceLoader.loadResource(resourcePath);
        } else {
            return getClass().getResourceAsStream(resourcePath);
        }
    }
}
