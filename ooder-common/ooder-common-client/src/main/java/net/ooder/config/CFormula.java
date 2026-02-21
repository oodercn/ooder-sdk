/**
 * $RCSfile: CParameter.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/07/08 00:25:44 $
 * <p>
 * Copyright (C) 2025 ooder, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder, Inc.
 * Use is subject to license terms.
 */
/**
 * $RCSfile: CFormula.java,v $
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

import net.ooder.common.FormulaType;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 对应应用配置文件中的Parameter元素
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class CFormula implements Serializable {

    public CFormula() {

    }

    private String pluginId;

    private String projectName;

    private String parameterId;

    private String name;

    private FormulaType parameterValue;

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getParameterId() {

        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParameterValue(FormulaType parameterValue) {
        this.parameterValue = parameterValue;
    }

    public FormulaType getParameterValue() {
        return parameterValue;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
