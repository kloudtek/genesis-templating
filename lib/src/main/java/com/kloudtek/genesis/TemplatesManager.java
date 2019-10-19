package com.kloudtek.genesis;

import com.kloudtek.util.UnexpectedException;
import com.kloudtek.util.xml.XmlUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
                Unmarshaller unmarshaller = getUnmarshaller(Templates.class);
                Templates templateList = (Templates) unmarshaller.unmarshal(url);
                logger.debug("Template file contains "+templateList.getTemplates().size()+" templates");
                for (Template template : templateList.getTemplates()) {
                    String templateId = template.getId();
                    if( templateId == null ) {
                        throw new InvalidTemplateException("Template missing id");
                    }
                    if( template.getName() == null ) {
                        template.setName(templateId);
                    }
                    if (templates.containsKey(templateId)) {
                        throw new InvalidTemplateException("Found duplicate templates for : " + templateId);
                    }
                    templates.put(templateId, template);
                    templatesUrl.put(templateId, url);
                    logger.debug("Loaded template " + templateId+" : "+template.getName());
                }
            } catch (JAXBException e) {
                throw new InvalidTemplateException("Invalid genesis template file: " + url + ": " + e.getMessage(), e);
            }
        }
    }


    public TemplateExecutor createExecutor(String templateName) throws TemplateNotFoundException, InvalidTemplateException {
        Template template = getTemplate(templateName);
        return new TemplateExecutor(template);
    }

    @NotNull
    public Template getTemplate(String templateName) throws TemplateNotFoundException, InvalidTemplateException {
        Unmarshaller unmarshaller = getUnmarshaller(Template.class);
        try {
            return (Template) unmarshaller.unmarshal(new URL(templateName));
        } catch (JAXBException e) {
            if( !templates.containsKey(templateName)) {
                throw new InvalidTemplateException(e);
            }
        } catch (MalformedURLException e) {
            //
        }
        File file = new File(templateName);
        if( file.exists() ) {
            try {
                return (Template) unmarshaller.unmarshal(file);
            } catch (JAXBException e) {
                throw new InvalidTemplateException(e);
            }
        }
        Template template = templates.get(templateName);
        if (template == null) {
            throw new TemplateNotFoundException("Unable to find template " + templateName);
        }
        return template;
    }

    private Unmarshaller getUnmarshaller(Class<?> cl) {
        try {
            return XmlUtils.createJAXBUnmarshaller(cl);
        } catch (JAXBException e) {
            throw new UnexpectedException(e);
        }
    }
}
