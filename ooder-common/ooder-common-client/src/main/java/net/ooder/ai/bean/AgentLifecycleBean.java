/**
 * $RCSfile: AgentLifecycleBean.java,v $
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

import net.ooder.annotation.LifecyclePhase;

import java.io.Serializable;

/**
 * AgentLifecycle注解实体映射对象
 * 符合 ooder bean规范，用于运行时存储@AgentLifecycle注解的元数据
 */
public class AgentLifecycleBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 生命周期阶段
     */
    private LifecyclePhase phase;

    /**
     * 执行顺序
     */
    private int order;

    // 默认构造函数
    //
    public AgentLifecycleBean() {
    }

    // 全参构造函数
    public AgentLifecycleBean(LifecyclePhase phase, int order) {
        this.phase = phase;
        this.order = order;
    }

    // Getter和Setter方法
    public LifecyclePhase getPhase() {
        return phase;
    }

    public void setPhase(LifecyclePhase phase) {
        this.phase = phase;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}