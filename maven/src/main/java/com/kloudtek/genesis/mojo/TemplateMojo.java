package com.kloudtek.genesis.mojo;

import com.kloudtek.genesis.InvalidTemplateException;
import com.kloudtek.genesis.Template;
import com.kloudtek.genesis.TemplateExecutionException;
import com.kloudtek.genesis.TemplateNotFoundException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Goal which touches a timestamp file.
 */
@Mojo( name = "template", defaultPhase = LifecyclePhase.INITIALIZE )
public class TemplateMojo extends AbstractMojo {
    @Parameter(defaultValue = "${genesistemplating.template}", property = "template", required = true)
    private String template;
    @Parameter(defaultValue = "${genesistemplating.target}", property = "target")
    private File target;
    @Parameter(defaultValue = "${genesistemplating.variables}", property = "vars")
    private Map<String,String> vars;
    @Parameter(defaultValue = "${genesistemplating.nonInteractive}", property = "nonInteractive")
    private boolean nonInteractive;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public File getTarget() {
        return target;
    }

    public void setTarget(File target) {
        this.target = target;
    }

    public Map<String, String> getVars() {
        return vars;
    }

    public void setVars(Map<String, String> vars) {
        this.vars = vars;
    }

    public void execute() throws MojoExecutionException {
        try {
            Template t = Template.create(template);
            if( vars != null ) {
                t.addVariables(vars);
            }
            t.setNonInteractive(nonInteractive);
            t.generate(target);
        } catch (TemplateNotFoundException|InvalidTemplateException|IOException|TemplateExecutionException e) {
            throw new MojoExecutionException(e.getMessage(),e);
        }
    }
}
