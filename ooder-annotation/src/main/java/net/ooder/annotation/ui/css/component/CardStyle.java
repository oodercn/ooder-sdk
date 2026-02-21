package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.CardPreset;

import java.lang.annotation.*;

/**
 * Card组件样式注解
 * 用于快速应用卡片预设样式，同时支持自定义覆盖
 *
 * 使用示例：
 * <pre>
 * // 使用预设样式
 * @CardStyle(preset = CardPreset.MATERIAL)
 * private CardUIComponent productCard;
 *
 * // 使用预设并覆盖特定属性
 * @CardStyle(
 *     preset = CardPreset.ELEMENT_SHADOW,
 *     layout = @CSLayout(width = "300px"),
 *     border = @CSBorder(borderRadius = "12px")
 * )
 * private CardUIComponent customCard;
 *
 * // 统计卡片
 * @CardStyle(preset = CardPreset.STAT_CARD)
 * private CardUIComponent statCard;
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CardStyle {

    CardPreset preset() default CardPreset.UNSET;

    CSFont font() default @CSFont;

    CSLayout layout() default @CSLayout;

    CSBorder border() default @CSBorder;
}
