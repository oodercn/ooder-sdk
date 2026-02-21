/**
 * $RCSfile: WorkflowStepBean.java,v $
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

import net.ooder.annotation.PriorityLevel;
import net.ooder.annotation.StepType;

import java.io.Serializable;
import java.util.List;

/**
 * WorkflowStep注解对应的Bean类
 */
public class WorkflowStepBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stepId;
    private String name;
    private StepType type;
    private PriorityLevel priority;
    private List<String> preSteps;
    private List<String> nextSteps;
    private boolean async;
    private long timeout;

    // 构造函数
    public WorkflowStepBean() {
        this.priority = PriorityLevel.MEDIUM;
        this.async = false;
        this.timeout = 60000; // 默认超时1分钟
    }

    // Getter和Setter方法
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public StepType getType() { return type; }
    public void setType(StepType type) { this.type = type; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public List<String> getPreSteps() { return preSteps; }
    public void setPreSteps(List<String> preSteps) { this.preSteps = preSteps; }

    public List<String> getNextSteps() { return nextSteps; }
    public void setNextSteps(List<String> nextSteps) { this.nextSteps = nextSteps; }

    public boolean isAsync() { return async; }
    public void setAsync(boolean async) { this.async = async; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }
}