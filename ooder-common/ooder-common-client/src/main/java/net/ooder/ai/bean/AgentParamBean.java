/**
 * $RCSfile: AgentParamBean.java,v $
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
package net.ooder.ai.bean;

import net.ooder.annotation.ValidationRule;

import java.io.Serializable;

/**
 * AgentParam注解实体映射对象
 * 符合 ooder bean规范，用于运行时存储@AgentParam注解的元数据
 */
public class AgentParamBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 验证规则
     */
    private ValidationRule validation;

    public AgentParamBean() {
    }

    public AgentParamBean(String name, String description, boolean required, String defaultValue, ValidationRule validation) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.validation = validation;
    }

    // Getter和Setter方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ValidationRule getValidation() {
        return validation;
    }

    public void setValidation(ValidationRule validation) {
        this.validation = validation;
    }
}