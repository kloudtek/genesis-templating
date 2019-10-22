package com.kloudtek.genesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DirectoryResourceLoader implements ResourceLoader {
    private File dir;

    public DirectoryResourceLoader(File dir) {
        this.dir = dir;
    }

    @Override
    public InputStream loadResource(String resourcePath) {
        File f = new File(dir + File.separator + resourcePath.replace("/", File.separator));
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            //
        }
        return null;
    }
}
