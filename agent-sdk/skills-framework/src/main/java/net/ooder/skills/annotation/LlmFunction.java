package net.ooder.skills.annotation;

import java.lang.annotation.*;

/**
 * LLM 函数注解
 * 
 * <p>用于标记方法可作为 LLM 的 Function Calling 目标</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * @LlmFunction(
 *     name = "searchDocuments",
 *     description = "搜索文档内容",
 *     capability = "documentSearch"
 * )
 * public SearchResult search(@LlmParam(name = "query") String query) {
 *     // ...
 * }
 * </pre>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmFunction {
    
    /**
     * 函数名称
     * <p>如果不指定，默认使用方法名</p>
     */
    String name() default "";
    
    /**
     * 函数描述
     * <p>用于 LLM 理解函数用途</p>
     */
    String description();
    
    /**
     * 关联的能力ID
     * <p>与 SKILLS.MD 中的 capability 对应</p>
     */
    String capability() default "";
    
    /**
     * 是否异步执行
     */
    boolean async() default false;
    
    /**
     * 超时时间（毫秒）
     */
    long timeout() default 30000;
}
