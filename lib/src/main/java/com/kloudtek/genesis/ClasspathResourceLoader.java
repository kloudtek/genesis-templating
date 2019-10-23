package com.kloudtek.genesis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

public class ClasspathResourceLoader implements ResourceLoader {
    public ClasspathResourceLoader() throws TemplateNotFoundException, IOException {
        Enumeration<URL> en = ClassLoader.getSystemResources("genesis-template.json");
        if( ! en.hasMoreElements() ) {
            throw new TemplateNotFoundException("No template in classpath found");
        }
        URL url = en.nextElement();
        if(en.hasMoreElements()) {
            throw new TemplateNotFoundException("More than one template found in classpath: "+url+" and "+en.nextElement());
        }
    }

    @Override
    public InputStream loadResource(String resourcePath) throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream(resourcePath);
        if( is == null ) {
            is = getClass().getResourceAsStream(resourcePath);
        }
        return is;
    }

    @Override
    public Set<String> listFiles() throws IOException {
        return Collections.emptySet();
    }
}
