package com.kloudtek.genesis;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import java.io.File;

public abstract class FSObj {
    @XmlAttribute
    protected String path;
    @XmlValue
    protected String content;
    @XmlAttribute
    protected String ignore;
    @XmlTransient
    protected File file;
    @XmlTransient
    protected Template template;

    public abstract void create(File target) throws TemplateExecutionException;

    public void setTemplate(Template template) {
        this.template = template;
    }

    public boolean isConflict() {
        return file.exists();
    }

    public void process(File target) throws TemplateExecutionException {
        path = template.process(path);
        file = new File(target + File.separator + path);
        ignore = template.process(ignore);
    }
}
