package com.kloudtek.genesis;

import java.io.*;
import java.util.*;

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

    @Override
    public Set<String> listFiles() {
        HashSet<String> results = new HashSet<>();
        buildFileList(results, "", new File(dir, "files"));
        return results;
    }

    public void buildFileList( HashSet<String> results, String basePath, File dir ) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if( file.isDirectory() ) {
                buildFileList(results,file.getName()+"/",file);
            } else {
                results.add(basePath+file.getName());
            }
        }
    }
}
