/**
 * $RCSfile: ParallelBean.java,v $
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

import net.ooder.annotation.ParallelFailureStrategy;
import net.ooder.annotation.ParallelWaitStrategy;

import java.io.Serializable;
import java.util.List;

/**
 * Parallel注解对应的Bean类
 */
public class ParallelBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> stepIds;
    private ParallelWaitStrategy waitStrategy;
    private ParallelFailureStrategy failureStrategy;
    private long timeout;
    private int threadPoolSize;

    // 构造函数
    public ParallelBean() {
        this.waitStrategy = ParallelWaitStrategy.ALL_COMPLETED;
        this.failureStrategy = ParallelFailureStrategy.ABORT_ON_FIRST_FAILURE;
        this.timeout = 180000; // 默认超时3分钟
        this.threadPoolSize = 5;
    }

    // Getter和Setter方法
    public List<String> getStepIds() { return stepIds; }
    public void setStepIds(List<String> stepIds) { this.stepIds = stepIds; }

    public ParallelWaitStrategy getWaitStrategy() { return waitStrategy; }
    public void setWaitStrategy(ParallelWaitStrategy waitStrategy) { this.waitStrategy = waitStrategy; }

    public ParallelFailureStrategy getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(ParallelFailureStrategy failureStrategy) { this.failureStrategy = failureStrategy; }

    public long getTimeout() { return timeout; }
    public void setTimeout(long timeout) { this.timeout = timeout; }

    public int getThreadPoolSize() { return threadPoolSize; }
    public void setThreadPoolSize(int threadPoolSize) { this.threadPoolSize = threadPoolSize; }
}