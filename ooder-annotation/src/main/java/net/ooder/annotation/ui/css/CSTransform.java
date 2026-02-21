package net.ooder.annotation.ui.css;

import java.lang.annotation.*;

/**
 * CSS 变换与动画样式注解
 * 对应虚拟DOM类型：transform, animation
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSTransform {
    
    /**
     * 2D/3D 变换
     */
    String transform() default "";
    
    /**
     * 变换原点
     */
    String transformOrigin() default "";
    
    /**
     * 变换样式（保留3D）
     */
    String transformStyle() default "";
    
    /**
     * 透视
     */
    String perspective() default "";
    
    /**
     * 透视原点
     */
    String perspectiveOrigin() default "";
    
    /**
     * 背面可见性
     */
    String backfaceVisibility() default "";
    
    /**
     * 过渡属性
     */
    String transitionProperty() default "";
    
    /**
     * 过渡持续时间
     */
    String transitionDuration() default "";
    
    /**
     * 过渡时间函数
     */
    String transitionTimingFunction() default "";
    
    /**
     * 过渡延迟
     */
    String transitionDelay() default "";
    
    /**
     * 过渡简写
     */
    String transition() default "";
    
    /**
     * 动画名称
     */
    String animationName() default "";
    
    /**
     * 动画持续时间
     */
    String animationDuration() default "";
    
    /**
     * 动画时间函数
     */
    String animationTimingFunction() default "";
    
    /**
     * 动画延迟
     */
    String animationDelay() default "";
    
    /**
     * 动画迭代次数
     */
    String animationIterationCount() default "";
    
    /**
     * 动画方向
     */
    String animationDirection() default "";
    
    /**
     * 动画填充模式
     */
    String animationFillMode() default "";
    
    /**
     * 动画播放状态
     */
    String animationPlayState() default "";
    
    /**
     * 动画简写
     */
    String animation() default "";
}
