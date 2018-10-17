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
    public void create(TemplateExecutor exec, File target) throws TemplateExecutionException {
        if( ignore != null ) {
            if( ignore.equalsIgnoreCase("true") ) {
                return;
            } else if( ignore.equalsIgnoreCase("false") ) {
                throw new TemplateExecutionException("Invalid ignore value: "+ignore);
            }
        }
        if( ! file.exists() ) {
            try {
                FileUtils.mkdirs(file);
            } catch (IOException e) {
                throw new TemplateExecutionException(e);
            }
        }
        if( files != null ) {
            for (FSObj f : files) {
                f.create(exec, file);
            }
        }
    }


    @Override
    public void process(TemplateExecutor exec, File target) throws TemplateExecutionException {
        super.process(exec, target);
        if( files != null ) {
            for (FSObj f : files) {
                f.process(exec, file);
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
