package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.URLBuilder;
import com.kloudtek.util.io.IOUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import java.io.*;

public class TFile {
    protected String path;
    protected String content;
    protected String ignore;
    @XmlTransient
    protected File file;
    @XmlTransient
    protected Template template;
    @XmlAttribute
    private Boolean process;
    @XmlAttribute
    private String encoding;
    @XmlAttribute
    private String resource;

    public TFile() {
    }

    public TFile(String path) {
        this.path = path;
    }

    public void create(TemplateExecutor exec, File target) throws TemplateExecutionException {
        try {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                FileUtils.mkdirs(parent);
            }
            try (FileOutputStream os = new FileOutputStream(file); InputStream is = getContent(exec)) {
                IOUtils.copy(is, os);
            }
        } catch (IOException e) {
            throw new TemplateExecutionException(e);
        }
    }

    private InputStream getContent(TemplateExecutor exec) throws TemplateExecutionException {
        try {
            if (StringUtils.isNotBlank(resource)) {
                String resourcePath = exec.filter(resource);
                InputStream is = template.getFileResource(resourcePath);
                if( is == null ) {
                    throw new TemplateExecutionException("File resource missing: " + resourcePath);
                }
                if (process != null && ! process) {
                    // we don't need to process so return the stream immediately
                    return is;
                }
                content = IOUtils.toString(is, getEncoding());
            }
            if (content == null) {
                throw new TemplateExecutionException("Content missing: " + path);
            }
            if (process == null || process) {
                return new ByteArrayInputStream(exec.filter(content).getBytes(getEncoding()));
            } else {
                return new ByteArrayInputStream(content.getBytes(getEncoding()));
            }
        } catch (IOException e) {
            throw new TemplateExecutionException(e);
        }
    }

    public String getEncoding() {
        return encoding != null ? encoding : "UTF-8";
    }


    public void setTemplate(Template template) {
        this.template = template;
    }

    public boolean isConflict() {
        return file.exists();
    }

    public void process(TemplateExecutor exec, File target, Template template) throws TemplateExecutionException {
        this.template = template;
        path = exec.filter(path);
        file = new File(target + File.separator + path);
        ignore = exec.filter(ignore);
    }

    public Boolean getProcess() {
        return process;
    }

    public void setProcess(Boolean process) {
        this.process = process;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIgnore() {
        return ignore;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

}
