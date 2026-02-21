
/**
 * $RCSfile: BPDPlugin.java,v $
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

import net.ooder.annotation.ServiceStatus;
import net.ooder.common.PluginType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 对应应用配置文件中的BPDElement元素
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: ITJDS
 * </p>
 *
 * @author  ooder
 * @version 1.0
 */
public class BPDPlugin implements Serializable {

    private String name;

    private String pluginId;

    private String projectId;

    private ServiceStatus status = ServiceStatus.normal;

    private ActivityDefImpl activityType;

    private String implementation;

    private PluginType pluginType;


    private String displayname;

    public PluginType getPluginType() {
        return pluginType;
    }

    public void setPluginType(PluginType pluginType) {
        this.pluginType = pluginType;
    }

    private Integer height = 350;

    private Integer width = 400;

    private Map<String, CParameter> parameters = new HashMap<String, CParameter>();


    private Map<String, CFormula> formulaTypeMap = new HashMap<String, CFormula>();

    private List<CBPDBrowserElement> browserElements = new ArrayList<CBPDBrowserElement>();

    private Map<String, CExtendedAttribute> extendedAttributes = new HashMap<String, CExtendedAttribute>();

    public String getPluginId() {

        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public BPDPlugin() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActivityDefImpl getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityDefImpl activityType) {
        this.activityType = activityType;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public void putParameter(String paramterId, CParameter parameter) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(paramterId, parameter);
    }

    public void putFormula(String parameterId, CFormula formula) {
        if (formulaTypeMap == null) {
            formulaTypeMap = new HashMap<>();
        }
        formulaTypeMap.put(parameterId, formula);
    }


    public void putExtendedAttribute(String name, CExtendedAttribute extendedAttribute) {
        extendedAttributes.put(name, extendedAttribute);
    }

    public Map<String, CFormula> getFormulaTypeMap() {
        return formulaTypeMap;
    }

    public void setFormulaTypeMap(Map<String, CFormula> formulaTypeMap) {
        this.formulaTypeMap = formulaTypeMap;
    }

    public String getParameterValue(String name) {
        return parameters.get(name).getParameterValue();
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Map<String, CParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, CParameter> parameters) {
        this.parameters = parameters;
    }

    public Map<String, CExtendedAttribute> getExtendedAttributes() {
        return extendedAttributes;
    }

    public void setExtendedAttributes(Map<String, CExtendedAttribute> extendedAttributes) {
        extendedAttributes = extendedAttributes;
    }


    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public List<CBPDBrowserElement> getBrowserElements() {
        return browserElements;
    }

    public void setBrowserElements(List<CBPDBrowserElement> browserElements) {
        this.browserElements = browserElements;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
