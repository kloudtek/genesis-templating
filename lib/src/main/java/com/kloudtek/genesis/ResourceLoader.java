package com.kloudtek.genesis;

import java.io.InputStream;

public interface ResourceLoader {
    InputStream loadResource(String resourcePath);
}
