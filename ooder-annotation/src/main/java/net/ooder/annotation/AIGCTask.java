package net.ooder.annotation;

import java.lang.annotation.*;



/**
 * AIGC任务注解
 * 标识AI生成任务方法及参数配置
 */
@Target({ElementType.TYPE, ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AIGCTask {
    /**
     * 任务ID
     */
    String taskId();

    /**
     * 任务名称
     */
    String name();

    /**
     * 任务描述
     */
    String description() default "";

    /**
     * 关联模型ID
     */
    String modelId();

    /**
     * 超时时间(毫秒)
     */
    int timeout() default 60000;

    /**
     * 重试次数
     */
    int retryCount() default 0;

    /**
     * 是否启用缓存
     */
    boolean cacheEnabled() default false;

    /**
     * 缓存过期时间(秒)
     */
    int cacheTTL() default 3600;

    /**
     * 任务优先级
     */
    PriorityLevel priority() default PriorityLevel.NORMAL;

    /**
     * 执行模式
     */
    ExecutionMode executionMode() default ExecutionMode.SYNC;

    /**
     * 资源配额配置
     * @deprecated 请使用cpuQuota, memQuota和gpuQuota
     */
    @Deprecated
    String resourceQuota() default "";

    /**
     * CPU配额(核)
     */
    int cpuQuota() default 1;

    /**
     * 内存配额(GB)
     */
    int memQuota() default 2;

    /**
     * GPU配额(数量)
     */
    int gpuQuota() default 0;

    /**
     * 任务依赖
     */
    String[] dependencies() default {};

    /**
     * 任务类型
     */
    TaskType type();

}
