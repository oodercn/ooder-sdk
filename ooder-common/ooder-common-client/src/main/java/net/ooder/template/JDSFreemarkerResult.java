
/**
 * $RCSfile: JDSFreemarkerResult.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.template;

import net.ooder.jds.core.esb.util.ActionContext;
import net.ooder.jds.core.esb.util.ValueStack;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;


public class JDSFreemarkerResult {

    private static final long serialVersionUID = -3778230771704661631L;


    protected Configuration configuration;
    protected ObjectWrapper wrapper;
    protected JDSFreemarkerManager freemarkerManager;
    private Writer writer;

    protected String location;
    private String pContentType = "text/html";

    public JDSFreemarkerResult() {
        super();
        this.freemarkerManager = new JDSFreemarkerManager();
    }


    public JDSFreemarkerResult(String location) {
        this.location = location;

    }


    public void setFreemarkerManager(JDSFreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }

    public void setContentType(String aContentType) {
        pContentType = aContentType;
    }


    public String getContentType() {
        return pContentType;
    }

    public Writer doExecute(String location,String templatePath) throws IOException, TemplateException {
        this.location = location;
        this.configuration = getConfiguration(templatePath);
        this.wrapper = getObjectWrapper();
        this.writer = new StringWriter();
        Template template = configuration.getTemplate(location);
        TemplateModel model = createModel();

        // Give subclasses a chance to hook into preprocessing
        if (preTemplateProcess(template, model)) {
            try {
                // Process the template
                template.process(model, writer);
            } finally {
                // Give subclasses a chance to hook into postprocessing
                postTemplateProcess(template, model);
            }
        }
        return writer;
    }

    public void doExecute(String location, Writer writer,String templatePath) throws IOException, TemplateException {
        this.location = location;
        this.configuration = getConfiguration(templatePath);
        this.wrapper = getObjectWrapper();

        Template template = configuration.getTemplate(location);
        TemplateModel model = createModel();

        // Give subclasses a chance to hook into preprocessing
        if (preTemplateProcess(template, model)) {
            try {
                // Process the template
                template.process(model, writer);
            } finally {
                // Give subclasses a chance to hook into postprocessing
                postTemplateProcess(template, model);
            }
        }
    }


    protected Configuration getConfiguration(String templatePath) throws TemplateException {
        return freemarkerManager.getConfiguration(templatePath);
    }


    protected ObjectWrapper getObjectWrapper() {
        return configuration.getObjectWrapper();
    }


    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * The default writer writes directly to the response writer.
     */
    public Writer getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        return null;
    }


    protected TemplateModel createModel() throws TemplateModelException {

        ValueStack stack = ActionContext.getContext().getValueStack();


        return freemarkerManager.buildTemplateModel(stack, wrapper);
    }


    /**
     * the default implementation of postTemplateProcess applies the contentType parameter
     */
    protected void postTemplateProcess(Template template, TemplateModel data) throws IOException {
    }


    protected boolean preTemplateProcess(Template template, TemplateModel model) throws IOException {
        Object attrContentType = template.getCustomAttribute("content_type");

        if (attrContentType != null) {

        } else {
            String contentType = getContentType();

            if (contentType == null) {
                contentType = "text/html";
            }

            String encoding = template.getEncoding();
            if (encoding != null) {
                contentType = contentType + "; charset=" + encoding;
            }
        }

        return true;
    }
}
