package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.InputPreset;

import java.lang.annotation.*;

/**
 * Input组件样式注解
 * 用于快速应用输入框预设样式，同时支持自定义覆盖
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性 (layout=@CSLayout(...), border=@CSBorder(...))
 * 3. 预设样式 (InputPreset.XXX)
 * 4. JS组件默认样式 (Input.js)
 *
 * 【零注解默认值】(当没有任何注解时，Input.js提供的默认样式):
 * - height: "32px"
 * - padding: "4px 11px"
 * - border: "1px solid #d9d9d9"
 * - borderRadius: "6px"
 * - backgroundColor: "#ffffff"
 * - fontSize: "14px"
 * - color: "rgba(0,0,0,0.88)"
 * - 聚焦状态: borderColor="#1677ff", boxShadow="0 0 0 2px rgba(5,145,255,0.1)"
 *
 * 【预设样式对照表】:
 * ┌─────────────────────┬──────────┬─────────────┬────────────┬──────────────────┐
 * │ 预设                │ 高度     │ 边框        │ 圆角       │ 背景色           │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ ANT_DEFAULT         │ 32px     │ 1px solid   │ 6px        │ #ffffff          │
 * │                     │          │ #d9d9d9     │            │                  │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ ANT_BORDERLESS      │ 32px     │ none        │ 0          │ transparent      │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ MATERIAL_STANDARD   │ 48px     │ bottom only │ 0          │ transparent      │
 * │                     │          │ 1px rgba    │            │                  │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ MATERIAL_FILLED     │ 48px     │ none        │ 4px 4px 0 0│ #f5f5f5          │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ MATERIAL_OUTLINED   │ 56px     │ 1px solid   │ 4px        │ #ffffff          │
 * │                     │          │ rgba        │            │                  │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ BOOTSTRAP_DEFAULT   │ 38px     │ 1px solid   │ 4px        │ #ffffff          │
 * │                     │          │ #ced4da     │            │                  │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ BOOTSTRAP_LARGE     │ 48px     │ 1px solid   │ 4px        │ #ffffff          │
 * │                     │          │ #ced4da     │            │                  │
 * └─────────────────────┴──────────┴─────────────┴────────────┴──────────────────┘
 *
 * 【使用模式】:
 * 模式1 - 纯预设: @InputStyle(preset = InputPreset.ANT_DEFAULT)
 * 模式2 - 覆盖预设: @InputStyle(preset = InputPreset.ANT_DEFAULT, layout = @CSLayout(width = "300px"))
 * 模式3 - 完全自定义: @InputStyle(layout = @CSLayout(...), border = @CSBorder(...))
 *
 * 【状态样式说明】:
 * - 聚焦状态: 通常由JS组件自动处理，边框变为主色
 * - 错误状态: 可通过border覆盖实现 border = @CSBorder(borderColor = "#ff4d4f")
 * - 禁用状态: 通常由JS组件根据disabled属性自动处理
 *
 * 【与ButtonStyle的区别】:
 * ⚠️ 注意: InputStyle和ButtonStyle是不同的注解
 *          Input用于文本输入，Button用于点击操作
 *          两者的预设值和默认样式不同
 *
 * 使用示例：
 * <pre>
 * // 使用预设样式
 * @InputStyle(preset = InputPreset.MATERIAL_OUTLINED)
 * private InputUIComponent usernameInput;
 *
 * // 使用预设并覆盖特定属性
 * @InputStyle(
 *     preset = InputPreset.ANT_DEFAULT,
 *     layout = @CSLayout(width = "300px"),
 *     border = @CSBorder(borderColor = "#ff0000")
 * )
 * private InputUIComponent customInput;
 *
 * // Material Design Filled样式
 * @InputStyle(preset = InputPreset.MATERIAL_FILLED)
 * private InputUIComponent searchInput;
 *
 * // Bootstrap大输入框
 * @InputStyle(preset = InputPreset.BOOTSTRAP_LARGE)
 * private InputUIComponent largeInput;
 *
 * // 无边框搜索框
 * @InputStyle(
 *     preset = InputPreset.ANT_DEFAULT,
 *     border = @CSBorder(border = "none", backgroundColor = "#f5f5f5"),
 *     layout = @CSLayout(borderRadius = "20px")
 * )
 * private InputUIComponent searchInput;
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InputStyle {

    /**
     * 输入框预设样式
     * 可选值: ANT_DEFAULT, ANT_BORDERLESS, MATERIAL_STANDARD, MATERIAL_FILLED,
     *        MATERIAL_OUTLINED, BOOTSTRAP_DEFAULT, BOOTSTRAP_LARGE
     * 默认值: UNSET (不应用任何预设，使用JS组件默认样式)
     *
     * 【预设详细说明】
     * - ANT_DEFAULT: Ant Design标准输入框，32px高，6px圆角
     * - ANT_BORDERLESS: 无边框输入框，透明背景
     * - MATERIAL_STANDARD: Material Design标准样式，仅底部边框
     * - MATERIAL_FILLED: Material Design填充样式，灰色背景
     * - MATERIAL_OUTLINED: Material Design描边样式，56px高
     * - BOOTSTRAP_DEFAULT: Bootstrap标准输入框，38px高
     * - BOOTSTRAP_LARGE: Bootstrap大输入框，48px高
     */
    InputPreset preset() default InputPreset.UNSET;

    /**
     * 字体样式覆盖
     * 用于覆盖预设或默认的字体相关样式
     * 如: @CSFont(color = "#1677ff", fontSize = "16px")
     *
     * 【常用覆盖】
     * - 修改文字颜色: @CSFont(color = "#ff4d4f") (错误提示色)
     * - 增大字体: @CSFont(fontSize = "16px")
     * - 调整字重: @CSFont(fontWeight = CSFontWeight.BOLD)
     */
    CSFont font() default @CSFont;

    /**
     * 布局样式覆盖
     * 用于覆盖预设或默认的布局相关样式
     * 如: @CSLayout(width = "300px", height = "40px")
     *
     * 【常用覆盖】
     * - 固定宽度: @CSLayout(width = "200px")
     * - 占满容器: @CSLayout(width = "100%")
     * - 调整高度: @CSLayout(height = "40px")
     * - 调整内边距: @CSLayout(padding = "8px 16px")
     */
    CSLayout layout() default @CSLayout;

    /**
     * 边框样式覆盖
     * 用于覆盖预设或默认的边框相关样式
     * 如: @CSBorder(borderColor = "#ff4d4f", borderRadius = "8px")
     *
     * 【常用覆盖】
     * - 错误状态: @CSBorder(borderColor = "#ff4d4f")
     * - 警告状态: @CSBorder(borderColor = "#faad14")
     * - 无边框: @CSBorder(border = "none")
     * - 圆角调整: @CSBorder(borderRadius = "20px")
     * - 背景色: @CSBorder(backgroundColor = "#f5f5f5")
     */
    CSBorder border() default @CSBorder;
}
