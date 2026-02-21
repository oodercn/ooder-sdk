package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.TabsPreset;

import java.lang.annotation.*;

/**
 * Tabs组件样式注解
 * 用于快速应用标签页预设样式，同时支持自定义覆盖
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TabsStyle {

    TabsPreset preset() default TabsPreset.UNSET;

    CSFont font() default @CSFont;

    CSLayout layout() default @CSLayout;

    CSBorder border() default @CSBorder;
}
