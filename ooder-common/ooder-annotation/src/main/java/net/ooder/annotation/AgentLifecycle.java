package net.ooder.annotation;



import java.lang.annotation.*;

/**
 * 标记Agent的生命周期回调方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AgentLifecycle {
    /**
     * 生命周期阶段
     */
    Stage value() default Stage.INIT;
    
    /**
     * 执行顺序，值越小越先执行
     */
    int order() default 0;

    LifecyclePhase phase();
    

}