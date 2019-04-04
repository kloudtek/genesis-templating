package com.kloudtek.genesis.mojo;

import com.kloudtek.genesis.*;
import com.kloudtek.util.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "template", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = false)
public class TemplateMojo extends AbstractMojo {
    @Parameter(defaultValue = "${genesis.template}", property = "template", required = true)
    private String template;
    @Parameter(defaultValue = "${genesis.target}", property = "target")
    private File target;
    @Parameter(property = "vars")
    private Map<String,String> vars;
    @Parameter(property = "defaults")
    private Map<String, String> defaults;
    @Parameter(defaultValue = "${genesis.nonInteractive}", property = "nonInteractive")
    private boolean nonInteractive;
    @Parameter(defaultValue = "${genesis.headless}", property = "headless")
    private boolean headless;
    @Parameter(defaultValue = "${genesis.skip}", property = "skip")
    private boolean skip;
    @Parameter(defaultValue = "${genesis.abort}", property = "abort")
    private String abort;

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

    public boolean isNonInteractive() {
        return nonInteractive;
    }

    public void setNonInteractive(boolean nonInteractive) {
        this.nonInteractive = nonInteractive;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public void execute() throws MojoExecutionException {
        if (!skip) {
            boolean disableHeadless = !headless && GraphicsEnvironment.isHeadless();
            try {
                if (disableHeadless) {
                    // !@#$@!#$!@#%$&!@#($&!@# anypoint / eclipse running maven headless
                    // brute forcing it back to system
                    try {
                        Field toolkit = Toolkit.class.getDeclaredField("toolkit");
                        toolkit.setAccessible(true);
                        toolkit.set(null, null);
                        Field headless = GraphicsEnvironment.class.getDeclaredField("headless");
                        headless.setAccessible(true);
                        headless.set(null, false);
                    } catch (Throwable e) {
                        getLog().error("Unable to get out of headless mode :(... template execution will probably fail if user input is required");
                    }
                }
                getLog().debug("Loading genesis template");
                TemplatesManager templatesManager = new TemplatesManager();
                Template template = templatesManager.getTemplate(this.template);
                TemplateExecutor exec = new TemplateExecutor(template);
                if (vars != null) {
                    exec.setVariables(vars);
                }
                if (defaults != null) {
                    exec.setDefaultOverrides(defaults);
                }
                exec.setHeadless(this.headless);
                exec.setNonInteractive(nonInteractive);
                getLog().info("Executing genesis template");
                exec.execute(target);
                getLog().info("Finished generate template project");
                if (StringUtils.isNotBlank(abort)) {
                    throw new MojoExecutionException(abort);
                }
            } catch (TemplateNotFoundException | InvalidTemplateException | IOException | TemplateExecutionException e) {
                getLog().error(e);
                throw new MojoExecutionException(e.getMessage(), e);
            }
        } else {
            getLog().info("Skipping genesis template");
        }
    }

    private Template executeTemplate() throws TemplateNotFoundException, InvalidTemplateException, IOException {
        return Template.create(template);
    }
}
