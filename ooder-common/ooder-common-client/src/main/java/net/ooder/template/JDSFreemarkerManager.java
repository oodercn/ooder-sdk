/*
 * $Id: FreemarkerManager.java,v 1.1 2025/07/08 00:25:54 Administrator Exp $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * $RCSfile: JDSFreemarkerManager.java,v $
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

import net.ooder.config.JDSUtil;
import net.ooder.context.JDSActionContext;
import net.ooder.jds.core.esb.util.FileManager;
import net.ooder.jds.core.esb.util.ValueStack;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JDSFreemarkerManager {

    private static final Log log = LogFactory.getLog(JDSFreemarkerManager.class);
    public static final String CONFIG_SERVLET_CONTEXT_KEY = "freemarker.Configuration";
    public static final String KEY_EXCEPTION = "exception";
    // coppied from freemarker servlet - so that there is no dependency on it
    public static final String KEY_APPLICATION = "Application";
    public static final String KEY_REQUEST_MODEL = "Request";
    public static final String KEY_SESSION_MODEL = "Session";
    public static final String KEY_JSP_TAGLIBS = "JspTaglibs";
    public static final String KEY_REQUEST_PARAMETER_MODEL = "Parameters";

    private String encoding = "utf-8";
    private boolean altMapWrapper;

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }


    public void setWrapperAltMap(String val) {
        altMapWrapper = "true".equals(val);
    }


    public final synchronized Configuration getConfiguration(String templatePath) throws TemplateException {

        if (templatePath == null) {
            try {
                templatePath = JDSUtil.getJdsRealPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Configuration config = (Configuration) JDSActionContext.getActionContext().getContext().get(templatePath);

        if (config == null) {
            config = createConfiguration(templatePath);
            JDSActionContext.getActionContext().getContext().put(templatePath, config);
        }

        config.setWhitespaceStripping(true);

        return config;
    }

    protected JDSScopesHashModel buildScopesHashModel(ObjectWrapper wrapper, ValueStack stack) {
        JDSScopesHashModel model = new JDSScopesHashModel(wrapper, stack);

        return model;
    }


    protected TemplateLoader getTemplateLoader(String templatePath) {
        // construct a FileTemplateLoader for the init-param 'TemplatePath'
        FileTemplateLoader templatePathLoader = null;
        try {
            //  String
            if (templatePath == null) {
                templatePath = JDSUtil.getJdsRealPath();
            }
            if (templatePath != null) {
                templatePathLoader = new FileTemplateLoader(new File(templatePath));
            }
        } catch (IOException e) {
            log.error("Invalid template path specified: " + e.getMessage(), e);
        }
        // presume that most apps will require the class and webapp template loader
        // if people wish to
        return templatePathLoader;
    }


    protected Configuration createConfiguration(String templatePath) throws TemplateException {
        Configuration configuration = new Configuration();

        configuration.setTemplateLoader(getTemplateLoader(templatePath));

        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        configuration.setObjectWrapper(new JDSBeanWrapper(true));
        if (encoding != null) {
            configuration.setDefaultEncoding(encoding);
        }

        loadSettings(configuration);

        return configuration;
    }

    /**
     * Load the settings from the /freemarker.properties file on the classpath
     *
     * @see Configuration#setSettings for the definition of valid settings
     */
    protected void loadSettings(Configuration configuration) {
        try {
            InputStream in = FileManager.loadFile("freemarker.properties", JDSFreemarkerManager.class);

            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                configuration.setSettings(p);
            }
        } catch (IOException e) {
            log.error("Error while loading freemarker settings from /freemarker.properties", e);
        } catch (TemplateException e) {
            log.error("Error while loading freemarker settings from /freemarker.properties", e);
        }
    }

    public SimpleHash buildTemplateModel(ValueStack stack, ObjectWrapper wrapper) {
        JDSScopesHashModel model = buildScopesHashModel(wrapper, stack);


        return model;
    }
}
