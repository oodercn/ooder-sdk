package net.ooder.skills.annotation;

import java.lang.annotation.*;

/**
 * LLM 参数注解
 * 
 * <p>用于标记方法参数，提供 LLM 所需的参数元数据</p>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * @LlmFunction(name = "searchDocuments", description = "搜索文档")
 * public SearchResult search(
 *     @LlmParam(name = "query", description = "搜索关键词", required = true) String query,
 *     @LlmParam(name = "limit", description = "返回数量", defaultValue = "10") int limit
 * ) {
 *     // ...
 * }
 * </pre>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmParam {
    
    /**
     * 参数名称
     * <p>如果不指定，默认使用参数名（需要编译时保留参数名）</p>
     */
    String name() default "";
    
    /**
     * 参数描述
     * <p>用于 LLM 理解参数用途</p>
     */
    String description() default "";
    
    /**
     * 参数类型
     * <p>用于 LLM 理解参数格式，如：string, number, boolean, array, object</p>
     */
    String type() default "";
    
    /**
     * 是否必需
     */
    boolean required() default true;
    
    /**
     * 默认值
     * <p>当参数非必需时，提供默认值</p>
     */
    String defaultValue() default "";
    
    /**
     * 枚举值列表
     * <p>当参数为枚举类型时，提供可选值</p>
     */
    String[] enumValues() default {};
    
    /**
     * 参数示例
     * <p>帮助 LLM 理解参数格式</p>
     */
    String example() default "";
}
