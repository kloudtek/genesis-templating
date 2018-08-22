package com.kloudtek.genesis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.util.concurrent.Callable;

public class GenesisCli implements Callable<Void> {
    private static final Logger logger = LoggerFactory.getLogger(GenesisCli.class);

    @Parameters(index = "0", description = "template file")
    private String templatePath;
    @Parameters(index = "1", description = "target directory")
    private File target;

    public static void main(String[] args) {
        CommandLine.call(new GenesisCli(), args);
    }

    public Void call() throws Exception {
        logger.info("Creating template using "+templatePath);
        Template template = Template.create(this.templatePath);
        template.generate(target);
        return null;
    }
}
