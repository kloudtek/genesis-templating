package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Directory extends FSObj {
    @XmlElements({
            @XmlElement(name = "file", type = TFile.class),
            @XmlElement(name = "dir", type = Directory.class)
    })
    private List<FSObj> files;

    @Override
    public void create(File target) throws TemplateExecutionException {
        File f = new File(target + File.separator + getPath() );
        if( ! f.exists() ) {
            try {
                FileUtils.mkdirs(f);
            } catch (IOException e) {
                throw new TemplateExecutionException(e);
            }
        }
        if( files != null ) {
            for (FSObj file : files) {
                file.create(f);
            }
        }
    }

    @Override
    public void setTemplate(Template template) {
        super.setTemplate(template);
        if( files != null ) {
            for (FSObj file : files) {
                file.setTemplate(template);
            }
        }
    }
}
