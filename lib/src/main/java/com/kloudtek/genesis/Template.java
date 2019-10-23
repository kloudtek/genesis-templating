package com.kloudtek.genesis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kloudtek.genesis.step.ConditionalSteps;
import com.kloudtek.genesis.step.Input;
import com.kloudtek.util.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@XmlRootElement
public class Template {
    private static final Logger logger = LoggerFactory.getLogger(Template.class);
    private String id;
    private String name;
    private String resourcePath;
    private List<Input> steps;
    private List<TFile> files;
    private boolean overwrite;
    private ResourceLoader resourceLoader;

    public Template() {
    }

    public Template(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
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
        File file = null;
        try {
            URL url = new URL(path);
            if (url.getProtocol().equalsIgnoreCase("file")) {
                file = new File(url.toURI());
            }else{
                if (lpath.endsWith(".json")) {
                    return loadJson(url);
                } else if (lpath.endsWith(".jar") || lpath.endsWith(".zip")) {

                }
            }
        } catch (MalformedURLException e) {
            file = new File(path);
        } catch (URISyntaxException e) {
            throw new UnexpectedException(e);
        }
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                return loadTemplate(new DirectoryResourceLoader(file));
            } else {
                if (lpath.endsWith(".json")) {
                    return loadJson(file);
                } else if (lpath.endsWith(".jar") || lpath.endsWith(".zip")) {
                    return loadTemplate(new ZipResourceLoader(file));
                }
            }
        } else {
            throw new TemplateNotFoundException("Template " + path + " not found");
        }
        return null;
    }

    private static Template loadTemplate(ResourceLoader resourceLoader) throws IOException, InvalidTemplateException {
        try (InputStream is = resourceLoader.loadResource("genesis-template.json")) {
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
        return objectMapper.readValue(is, Template.class);
    }

    private static Template loadJson(URL url) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(url, Template.class);
    }

    private static Template loadJson(File file) throws InvalidTemplateException {
        try {
            try (FileInputStream is = new FileInputStream(file)) {
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

    @JsonProperty
    public List<TFile> getFiles() {
        return files;
    }

    public void setFiles(List<TFile> files) {
        this.files = files;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public InputStream getFileResource(String resourcePath) throws IOException {
        return resourceLoader.loadResource("files/"+resourcePath);
    }

    public TFile addFile(String path) {
        TFile f = new TFile(path);
        f.setTemplate(this);
        files.add(f);
        return f;
    }
}
