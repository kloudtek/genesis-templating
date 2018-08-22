package com.kloudtek.genesis;

import java.io.InputStream;

public abstract class TemplateEngine {
    public abstract InputStream process(Template template, InputStream is) throws TemplateExecutionException;
}
