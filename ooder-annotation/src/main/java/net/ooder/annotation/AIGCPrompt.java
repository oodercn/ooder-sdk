package net.ooder.annotation;

import net.ooder.annotation.TemplateType;

import java.lang.annotation.*;

/**
 * AIGC提示工程注解
 * 定义提示模板、变量和生成参数
 */
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AIGCPrompt {
    /**
     * 提示模板字符串
     */
    String template();

    /**
     * 模板变量名称列表
     */
    String[] variables() default {};

    /**
     * 最大令牌数
     */
    int maxTokens() default 1024;

    /**
     * 温度参数，控制生成随机性
     */
    float temperature() default 0.7f;
    
    TemplateType templateType() default TemplateType.TEXT;
    /**
     * 提示工程版本
     */
    String version() default "1.0";

    /**
     * 是否启用模板缓存
     */
    boolean cacheable() default false;
}