package com.kloudtek.genesis;

import java.io.File;
import java.io.InputStream;

public class DirectoryResourceLoader implements ResourceLoader {
    private File dir;

    public DirectoryResourceLoader(File dir) {
        this.dir = dir;
    }

    @Override
    public InputStream loadResource(String resourcePath) {
        return null;
    }
}
