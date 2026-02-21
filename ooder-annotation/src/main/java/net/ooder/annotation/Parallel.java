package net.ooder.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Parallel {
    /** 并行步骤ID列表 */
    String[] stepIds();
    
    /** 等待策略 */
    ParallelWaitStrategy waitStrategy() default ParallelWaitStrategy.ALL_COMPLETED;
    
    /** 失败策略 */
    ParallelFailureStrategy failureStrategy() default ParallelFailureStrategy.ABORT_ON_FIRST_FAILURE;
    
    /** 超时时间(毫秒) */
    long timeout() default 120000;
}
