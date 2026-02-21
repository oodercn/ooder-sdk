/**
 * $RCSfile: AIGCTaskBean.java,v $
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

import net.ooder.annotation.AIGCTask;
import net.ooder.annotation.AnnotationType;
import net.ooder.annotation.CustomBean;
import net.ooder.web.util.AnnotationUtil;


/**
 * AIGC任务注解对应的Bean类
 */
@AnnotationType(clazz = AIGCTask.class)
public class AIGCTaskBean implements CustomBean {
    private String taskId;
    private String name;
    private String description;
    private String modelId;
    private int timeout;
    private int retryCount; // 新增属性
    private boolean cacheEnabled;
    private int cacheTTL;
    private String resourceQuota;
    private int cpuQuota = 1;
    private int memQuota = 2;
    private int gpuQuota = 0;
    private String[] dependencies = {};

    // Getters and setters
    public int getCpuQuota() { return cpuQuota; }
    public void setCpuQuota(int cpuQuota) { this.cpuQuota = cpuQuota; }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public int getCacheTTL() {
        return cacheTTL;
    }

    public void setCacheTTL(int cacheTTL) {
        this.cacheTTL = cacheTTL;
    }

   

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getResourceQuota() {
        return resourceQuota;
    }

    public void setResourceQuota(String resourceQuota) {
        this.resourceQuota = resourceQuota;
    }

    @Override
    public String toAnnotationStr() {
        return AnnotationUtil.toAnnotationStr(this);
    }
}