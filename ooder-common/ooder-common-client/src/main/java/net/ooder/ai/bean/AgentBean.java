/**
 * $RCSfile: AgentBean.java,v $
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

import net.ooder.annotation.AgentDomain;

import java.io.Serializable;

/**
 * Agent注解实体映射对象
 * 符合 ooder bean规范，用于运行时存储@Agent注解的元数据
 */
public class AgentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 版本号
     */
    private String version;

    /**
     * 所属领域
     */
    private AgentDomain domain;

    public AgentBean() {
    }

    public AgentBean(String id, String name, String description, String version, AgentDomain domain) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.domain = domain;
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public AgentDomain getDomain() {
        return domain;
    }

    public void setDomain(AgentDomain domain) {
        this.domain = domain;
    }
}