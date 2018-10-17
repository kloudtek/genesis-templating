package com.kloudtek.genesis;

import com.kloudtek.util.xml.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

public class TemplatesManager {
    private static final Logger logger = LoggerFactory.getLogger(TemplatesManager.class);
    private HashMap<String, Template> templates = new HashMap<>();
    private HashMap<String, URL> templatesUrl = new HashMap<>();

    public TemplatesManager() throws IOException, InvalidTemplateException {
        Enumeration<URL> resources = ClassLoader.getSystemClassLoader().getResources("genesis-templates.xml");
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
                    templates.put(templateName,template);
                    templatesUrl.put(templateName,url);
                }
            } catch (JAXBException e) {
                throw new InvalidTemplateException("Invalid genesis template file: " + url + ": " + e.getMessage(), e);
            }
        }
    }

    public TemplateExecutor createExecutor(String templateName, File target) throws TemplateNotFoundException {
        Template template = templates.get(templateName);
        if( template == null ) {
            throw new TemplateNotFoundException("Unable to find template "+templateName);
        }
        return new TemplateExecutor(template,target);
    }
}
