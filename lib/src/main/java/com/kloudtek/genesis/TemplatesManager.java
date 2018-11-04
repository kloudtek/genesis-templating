package com.kloudtek.genesis;

import com.kloudtek.util.xml.XmlUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

public class TemplatesManager {
    private static final Logger logger = LoggerFactory.getLogger(TemplatesManager.class);
    private HashMap<String, Template> templates = new HashMap<>();
    private HashMap<String, URL> templatesUrl = new HashMap<>();

    public TemplatesManager() throws IOException, InvalidTemplateException {
        Enumeration<URL> resources = getClass().getClassLoader().getResources("genesis-templates.xml");
        if (!resources.hasMoreElements()) {
            logger.warn("No template descriptors found");
        }
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            logger.debug("Found template file " + url.toString());
            try {
                Unmarshaller unmarshaller = XmlUtils.createJAXBUnmarshaller(Templates.class);
                Templates templateList = (Templates) unmarshaller.unmarshal(url);
                for (Template template : templateList.getTemplates()) {
                    String templateName = template.getName();
                    if (templates.containsKey(templateName)) {
                        throw new InvalidTemplateException("Found duplicate templates: " + url + " and " + templates.get(templateName));
                    }
                    templates.put(templateName, template);
                    templatesUrl.put(templateName, url);
                    logger.debug("Loaded template " + templateName);
                }
            } catch (JAXBException e) {
                throw new InvalidTemplateException("Invalid genesis template file: " + url + ": " + e.getMessage(), e);
            }
        }
    }


    public TemplateExecutor createExecutor(String templateName) throws TemplateNotFoundException, TemplateExecutionException {
        Template template = getTemplate(templateName);
        return new TemplateExecutor(template);
    }

    @NotNull
    public Template getTemplate(String templateName) throws TemplateNotFoundException {
        Template template = templates.get(templateName);
        if (template == null) {
            throw new TemplateNotFoundException("Unable to find template " + templateName);
        }
        return template;
    }
}
