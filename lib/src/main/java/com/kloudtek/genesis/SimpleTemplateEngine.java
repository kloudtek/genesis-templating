package com.kloudtek.genesis;

import com.kloudtek.util.StringUtils;
import com.kloudtek.util.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SimpleTemplateEngine extends TemplateEngine {
    @Override
    public InputStream process(Template template, InputStream is) throws TemplateExecutionException {
        try {
            String data = IOUtils.toString(is);
            data = StringUtils.substituteVariables(data,template.getVariables());
            return new ByteArrayInputStream(data.getBytes());
        } catch (IOException e) {
            throw new TemplateExecutionException(e);
        }
    }
}
