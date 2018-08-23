package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.StringUtils;
import com.kloudtek.util.io.IOUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.io.*;

import static com.kloudtek.util.StringUtils.utf8;

public class TFile extends FSObj {
    @XmlAttribute
    private Boolean trim;
    @XmlAttribute
    private Boolean process;
    @XmlAttribute
    private String encoding;
    @XmlAttribute
    private String resource;

    public void create(File target) throws TemplateExecutionException {
        try {
            File fh = new File(target + File.separator + getPath());
            File parent = fh.getParentFile();
            if (!parent.exists()) {
                FileUtils.mkdirs(parent);
            }
            try (FileOutputStream os = new FileOutputStream(fh); InputStream is = getContent()) {
                IOUtils.copy(is, os);
            }
        } catch (IOException e) {
            throw new TemplateExecutionException(e);
        }
    }

    private InputStream getContent() throws TemplateExecutionException {
        try {
            if (StringUtils.isNotBlank(resource)) {
                if (!resource.startsWith("/")) {
                    resource = "/" + resource;
                }
                resource = template.process(resource);
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
                return new ByteArrayInputStream(template.process(content).getBytes(getEncoding()));
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
