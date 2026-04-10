package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.TablePreset;

import java.lang.annotation.*;

/**
 * Table组件样式注解
 * 用于快速应用表格预设样式，同时支持自定义覆盖
 *
 * 使用示例：
 * <pre>
 * // 使用预设样式
 * @TableStyle(preset = TablePreset.BORDERED)
 * private TableUIComponent dataTable;
 *
 * // 使用预设并覆盖特定属性
 * @TableStyle(
 *     preset = TablePreset.STRIPED,
 *     layout = @CSLayout(width = "100%")
 * )
 * private TableUIComponent customTable;
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableStyle {

    TablePreset preset() default TablePreset.UNSET;

    CSFont font() default @CSFont;

    CSLayout layout() default @CSLayout;

    CSBorder border() default @CSBorder;
}
