package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.io.IOUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlValue;
import java.io.*;
import java.util.List;

public abstract class FSObj {
    @XmlAttribute
    protected String path;
    protected File file;
    @XmlValue
    protected String content;
    protected Template template;

    public abstract void create(File target) throws TemplateExecutionException;

    public void setTemplate(Template template) {
        this.template = template;
    }

    public boolean isConflict() {
        return file.exists();
    }

    public void processPath(File target) throws TemplateExecutionException {
        path = template.process(path);
        file = new File(target + File.separator + path);
    }
}
