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
    @XmlValue
    protected String content;
    protected Template template;

    public String getPath() {
        return StringUtils.substituteVariables(path.replace('/', File.separatorChar), template.getVariables());
    }

    public abstract void create(File target) throws TemplateExecutionException;

    public void setTemplate(Template template) {
        this.template = template;
    }
}
