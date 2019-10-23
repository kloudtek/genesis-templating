package com.kloudtek.genesis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

public class GenesisCli implements Callable<Void> {
    private static final Logger logger = LoggerFactory.getLogger(GenesisCli.class);

    @Parameters(index = "0", description = "template")
    private String template;
    @Parameters(index = "1", description = "target directory")
    private File target;
    @Option(names = {"-a","--advanced"},description = "Enable advanced mode",defaultValue = "false")
    private boolean advanced;
    @Option(names = "-D")
    private Map<String,String> vars;

    public static void main(String[] args) {
        CommandLine.call(new GenesisCli(), args);
    }

    public Void call() throws Exception {
        logger.info("Creating template using "+template);
        TemplateExecutor executor = new TemplateExecutor(Template.create(template));
        executor.setVariables(vars);
        executor.setAdvanced(advanced);
        executor.execute(target);
        return null;
    }
}
