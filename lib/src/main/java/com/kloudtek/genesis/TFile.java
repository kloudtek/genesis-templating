package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;
import com.kloudtek.util.io.IOUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.io.*;

public class TFile {
    @XmlAttribute
    protected String path;
    @XmlValue
    private String content;
    @XmlAttribute
    private Boolean trim;

    public void create(Template template, File target) throws TemplateExecutionException {
        try {
            File fh = new File(target + File.separator + path.replace('/', File.separatorChar));
            File parent = fh.getParentFile();
            if (!parent.exists()) {
                FileUtils.mkdirs(parent);
            }
            try ( FileOutputStream os = new FileOutputStream(fh); InputStream is = getContent(template) ) {
                IOUtils.copy(is,os);
            }
        } catch (IOException e) {
            throw new TemplateExecutionException(e);
        }
    }

    private InputStream getContent(Template template) throws TemplateExecutionException {
        if( content == null ) {
            throw new TemplateExecutionException("Content missing from "+path);
        }
        if(trim == null || trim) {
            content = content.trim();
        }
        InputStream is = new ByteArrayInputStream(content.getBytes());
        TemplateEngine engine = template.getEngine("simple");
        is = engine.process(template, is);
        return is;
    }
}
