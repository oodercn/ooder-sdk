/**
 * $RCSfile: AgentActionBean.java,v $
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
 * AgentAction注解实体映射对象
 * 符合 ooder bean规范，用于运行时存储@AgentAction注解的元数据
 */
public class AgentActionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 操作名称
     */
    private String name;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 是否异步执行
     */
    private boolean async;

    /**
     * 超时时间(毫秒)
     */
    private int timeout;

    public AgentActionBean() {
    }

    public AgentActionBean(String name, String description, boolean async, int timeout) {
        this.name = name;
        this.description = description;
        this.async = async;
        this.timeout = timeout;
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

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}