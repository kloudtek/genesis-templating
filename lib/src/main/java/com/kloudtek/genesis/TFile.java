package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.io.IOUtils;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.*;

public class TFile extends FSObj {
    @XmlAttribute
    private Boolean trim;
    @XmlAttribute
    private Boolean process;
    @XmlAttribute
    private String encoding;
    @XmlAttribute
    private String resource;

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
                if (!resource.startsWith("/")) {
                    resource = "/" + resource;
                }
                resource = exec.filter(resource);
                try (InputStream is = getClass().getResourceAsStream(resource)) {
                    if (process == null || process) {
                        content = IOUtils.toString(is, getEncoding());
                    } else {
                        return is;
                    }
                }
            }
            if (content == null) {
                throw new TemplateExecutionException("Content missing from " + path);
            }
            if (trim == null || trim) {
                content = content.trim();
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
}
