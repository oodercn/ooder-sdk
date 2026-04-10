package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.TagPreset;

import java.lang.annotation.*;

/**
 * Tag组件样式注解
 * 用于快速应用标签预设样式，同时支持自定义覆盖
 *
 * 使用示例：
 * <pre>
 * // 使用预设样式
 * @TagStyle(preset = TagPreset.PRIMARY)
 * private TagUIComponent statusTag;
 *
 * // 使用预设并覆盖特定属性
 * @TagStyle(
 *     preset = TagPreset.SUCCESS,
 *     layout = @CSLayout(margin = "4px")
 * )
 * private TagUIComponent customTag;
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TagStyle {

    TagPreset preset() default TagPreset.UNSET;

    CSFont font() default @CSFont;

    CSLayout layout() default @CSLayout;

    CSBorder border() default @CSBorder;
}
