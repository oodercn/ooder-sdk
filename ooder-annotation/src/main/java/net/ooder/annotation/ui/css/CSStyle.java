package net.ooder.annotation.ui.css;

import java.lang.annotation.*;

/**
 * CSS 样式组合注解
 * 用于在组件类上定义完整的 CSS 样式配置
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSStyle {
    
    /**
     * CSS 类名
     */
    String className() default "";
    
    /**
     * 自定义 CSS 样式字符串
     */
    String customCss() default "";
    
    /**
     * 沙箱主题
     */
    String sandbox() default "";
    
    /**
     * 字体样式
     */
    CSFont font() default @CSFont();
    
    /**
     * 布局样式
     */
    CSLayout layout() default @CSLayout();
    
    /**
     * 边框样式
     */
    CSBorder border() default @CSBorder();
    
    /**
     * Flex 布局样式
     */
    CSFlex flex() default @CSFlex();
    
    /**
     * 变换与动画
     */
    CSTransform transform() default @CSTransform();
    
    /**
     * 正常状态样式
     */
    String normal() default "";
    
    /**
     * 悬停状态样式
     */
    String hover() default "";
    
    /**
     * 激活状态样式
     */
    String active() default "";
    
    /**
     * 焦点状态样式
     */
    String focus() default "";
    
    /**
     * 禁用状态样式
     */
    String disabled() default "";
}
