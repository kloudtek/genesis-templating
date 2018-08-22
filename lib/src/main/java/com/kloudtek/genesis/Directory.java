package com.kloudtek.genesis;

import com.kloudtek.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class Directory extends TFile {
    @Override
    public void create(Template template, File target) throws TemplateExecutionException {
        File f = new File(target + File.separator + path.replace('/',File.separatorChar));
        if( ! f.exists() ) {
            try {
                FileUtils.mkdirs(f);
            } catch (IOException e) {
                throw new TemplateExecutionException(e);
            }
        }
    }
}
