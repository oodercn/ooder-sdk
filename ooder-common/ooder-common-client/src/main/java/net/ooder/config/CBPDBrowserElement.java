/**
 * $RCSfile: CBPDBrowserElement.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:44 $
 * <p>
 * Copyright (C) 2005 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CBPDBrowserElement.java,v $
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
package net.ooder.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 对应应用配置文件中的BPDElement元素
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: odder
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
public class CBPDBrowserElement implements Serializable {


    public CBPDBrowserElement() {

    }


    private String browserId;

    private String baseurl;

    private String toxml;

    private String displayname;

    private String name;

    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String pluginId;

    public String getBrowserId() {
        if (browserId == null) {
            browserId = this.pluginId + "." + this.name;
        }
        return browserId;
    }

    public void setBrowserId(String browserId) {
        this.browserId = browserId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }


    private Map<String, CParameter> parameters = new HashMap<String, CParameter>();

    private Map<String, CExtendedAttribute> ExtendedAttributes = new HashMap<String, CExtendedAttribute>();


    public void putParameter(String name, CParameter parameter) {
        parameters.put(name, parameter);
    }

    public void putExtendedAttribute(String name, CExtendedAttribute extendedAttribute) {
        ExtendedAttributes.put(name, extendedAttribute);
    }

    public String getParameterValue(String name) {
        return parameters.get(name).getParameterValue();
    }


    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public Map<String, CParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, CParameter> parameters) {
        this.parameters = parameters;
    }

    public String getToxml() {
        return toxml;
    }

    public void setToxml(String toxml) {
        this.toxml = toxml;
    }

    public Map<String, CExtendedAttribute> getExtendedAttributes() {
        return ExtendedAttributes;
    }

    public void setExtendedAttributes(
            Map<String, CExtendedAttribute> extendedAttributes) {
        ExtendedAttributes = extendedAttributes;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
