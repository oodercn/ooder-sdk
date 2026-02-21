/**
 * $RCSfile: WorkflowBean.java,v $
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

import net.ooder.annotation.WorkflowType;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Workflow注解对应的Bean类
 */
public class WorkflowBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private WorkflowType type;
    private long timeout;
    private int retryCount;
    private boolean enabled;

    // 构造函数
    public WorkflowBean() {
        this.timeout = 300000; // 默认超时5分钟
        this.retryCount = 0;
        this.enabled = true;
    }

    // Getter和Setter方法
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public WorkflowType getType() { return type; }
    public void setType(WorkflowType type) { this.type = type; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}