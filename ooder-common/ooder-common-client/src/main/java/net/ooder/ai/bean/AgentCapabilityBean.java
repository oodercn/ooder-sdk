/**
 * $RCSfile: AgentCapabilityBean.java,v $
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

import java.io.Serializable;

/**
 * AgentCapability注解实体映射对象
 * 符合 ooder bean规范，用于运行时存储@AgentCapability注解的元数据
 */
public class AgentCapabilityBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 能力名称
     */
    private String name;

    /**
     * 版本号
     */
    private String version;

    /**
     * 提供者
     */
    private String provider;

    /**
     * 是否核心能力
     */
    private boolean isCore;

    /**
     * 配置参数
     */
    private String config;

    public AgentCapabilityBean() {
    }

    // 全参构造函数
    public AgentCapabilityBean(String name, String version, String provider, boolean isCore, String config) {
        this.name = name;
        this.version = version;
        this.provider = provider;
        this.isCore = isCore;
        this.config = config;
    }

    // Getter和Setter方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isCore() {
        return isCore;
    }

    public void setCore(boolean core) {
        isCore = core;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}