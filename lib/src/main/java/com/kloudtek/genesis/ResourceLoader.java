package com.kloudtek.genesis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface ResourceLoader {
    InputStream loadResource(String resourcePath) throws IOException;
    Set<String> listFiles() throws IOException;
}
