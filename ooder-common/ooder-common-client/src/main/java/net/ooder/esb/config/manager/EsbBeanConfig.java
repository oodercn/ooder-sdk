/**
 * $RCSfile: EsbBeanConfig.java,v $
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
package net.ooder.esb.config.manager;
/**
 * time 06-01-01
 *
 * @author wenzhang
 */

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class EsbBeanConfig {
    private static final long serialVersionUID = -1262262294095119331L;
    private String jspsrcpath;
    private String javabeansrcpath = EsbBeanFactory.formatFilePath(System.getProperty("user.dir") + File.separator+"src"+ File.separator+"main"+ File.separator+"java"+ File.separator);
    private String javabeanclasspath;
    private String reload = "true";
    private String actionContextClass = "net.ooder.server.context.SpringMvcContextImpl";
    private String compile;
    private Map<String, EsbBean> esbBeanMap = new HashMap<String, EsbBean>();

    /* Getter And Setter Function */
    public Map<String, EsbBean> getEsbBeanMap() {
        return esbBeanMap;
    }

    public void setEsbBeanMap(Map<String, EsbBean> esbBeanMap) {
        this.esbBeanMap = esbBeanMap;
    }

    public String getJavabeanclasspath() {
        return javabeanclasspath;
    }

    public void setJavabeanclasspath(String javabeanclasspath) {
        this.javabeanclasspath = javabeanclasspath;
    }

    public String getJavabeansrcpath() {
        return javabeansrcpath;
    }

    public void setJavabeansrcpath(String javabeansrcpath) {
        if (javabeansrcpath != null) {
            this.javabeansrcpath = javabeansrcpath;
        }
    }

    public String getJspsrcpath() {
        return jspsrcpath;
    }

    public void setJspsrcpath(String jspsrcpath) {
        this.jspsrcpath = jspsrcpath;
    }

    public String getReload() {
        return reload;
    }

    public void setReload(String reload) {
        this.reload = reload;
    }

    public String getCompile() {
        return compile;
    }

    public void setCompile(String compile) {
        this.compile = compile;
    }

    public String getActionContextClass() {
        return actionContextClass;
    }

    public void setActionContextClass(String actionContextClass) {
        this.actionContextClass = actionContextClass;
    }


}