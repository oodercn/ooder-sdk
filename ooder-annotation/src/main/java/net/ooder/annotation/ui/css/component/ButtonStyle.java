package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSFlex;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.ButtonPreset;

import java.lang.annotation.*;

/**
 * Button组件样式注解
 * 用于快速应用按钮预设样式，同时支持自定义覆盖
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性 (font=@CSFont(...), layout=@CSLayout(...))
 * 3. 预设样式 (ButtonPreset.XXX)
 * 4. JS组件默认样式 (Button.js)
 *
 * 【零注解默认值】(当没有任何注解时，Button.js提供的默认样式):
 * - display: "inline-flex"
 * - height: "32px"
 * - padding: "4px 15px"
 * - border: "1px solid #d9d9d9"
 * - borderRadius: "6px"
 * - backgroundColor: "#ffffff"
 * - color: "rgba(0,0,0,0.88)"
 * - fontSize: "14px"
 * - fontWeight: "400"
 * - cursor: "pointer"
 *
 * 【预设样式分类】:
 * 1. Material Design系列 (3种):
 *    - MATERIAL_CONTAINED: 实心按钮，主色背景，白色文字
 *    - MATERIAL_OUTLINED: 描边按钮，透明背景，主色边框
 *    - MATERIAL_TEXT: 文字按钮，无边框，主色文字
 *
 * 2. Ant Design系列 (4种):
 *    - ANT_PRIMARY: 主按钮，蓝色背景，白色文字
 *    - ANT_DEFAULT: 默认按钮，白色背景，灰色边框
 *    - ANT_DASHED: 虚线按钮，白色背景，虚线边框
 *    - ANT_LINK: 链接按钮，无边框，蓝色文字
 *
 * 3. Element Plus系列 (5种):
 *    - ELEMENT_PRIMARY: 主按钮，蓝色背景
 *    - ELEMENT_SUCCESS: 成功按钮，绿色背景
 *    - ELEMENT_WARNING: 警告按钮，橙色背景
 *    - ELEMENT_DANGER: 危险按钮，红色背景
 *    - ELEMENT_ROUND: 圆角按钮，大圆角(20px)
 *
 * 4. Bootstrap系列 (3种):
 *    - BOOTSTRAP_PRIMARY: 主按钮，蓝色背景
 *    - BOOTSTRAP_SECONDARY: 次要按钮，灰色背景
 *    - BOOTSTRAP_OUTLINE_PRIMARY: 描边主按钮
 *
 * 5. 特殊按钮 (3种):
 *    - ICON_BUTTON: 图标按钮，圆形，透明背景，40x40px
 *    - FAB: 浮动操作按钮，大尺寸(56x56px)，圆形
 *    - FAB_MINI: 迷你浮动按钮，小尺寸(40x40px)，圆形
 *
 * 【使用模式】:
 * 模式1 - 纯预设: @ButtonStyle(preset = ButtonPreset.MATERIAL_CONTAINED)
 * 模式2 - 覆盖预设: @ButtonStyle(
 *             preset = ButtonPreset.MATERIAL_OUTLINED,
 *             layout = @CSLayout(margin = "10px"),
 *             border = @CSBorder(borderRadius = "8px")
 *         )
 * 模式3 - 完全自定义: @ButtonStyle(
 *             font = @CSFont(color = "#fff", fontWeight = CSFontWeight.BOLD),
 *             layout = @CSLayout(padding = "12px 24px"),
 *             border = @CSBorder(backgroundColor = "#1976d2", borderRadius = "4px")
 *         )
 *
 * 【使用建议】:
 * 1. 主要操作: 使用 MATERIAL_CONTAINED / ANT_PRIMARY / ELEMENT_PRIMARY
 * 2. 次要操作: 使用 MATERIAL_OUTLINED / ANT_DEFAULT
 * 3. 文字链接: 使用 MATERIAL_TEXT / ANT_LINK
 * 4. 图标按钮: 使用 ICON_BUTTON
 * 5. 浮动操作: 使用 FAB / FAB_MINI
 * 6. 状态反馈: 使用 ELEMENT_SUCCESS/WARNING/DANGER
 *
 * 【属性冲突检测】⚠️ 重要
 * - preset 与 font/layout/border 同时设置时，显式设置的属性会覆盖预设
 * - 建议优先使用 preset，需要微调时再使用覆盖属性
 *
 * 使用示例：
 * <pre>
 * // 1. 使用预设样式 - 主要操作按钮
 * @ButtonStyle(preset = ButtonPreset.MATERIAL_CONTAINED)
 * private ButtonUIComponent submitBtn;
 *
 * // 2. 使用预设并覆盖特定属性 - 自定义圆角
 * @ButtonStyle(
 *     preset = ButtonPreset.MATERIAL_OUTLINED,
 *     layout = @CSLayout(margin = "10px"),
 *     border = @CSBorder(borderRadius = "8px")
 * )
 * private ButtonUIComponent customBtn;
 *
 * // 3. Ant Design主按钮
 * @ButtonStyle(preset = ButtonPreset.ANT_PRIMARY)
 * private ButtonUIComponent primaryBtn;
 *
 * // 4. Element Plus危险按钮
 * @ButtonStyle(preset = ButtonPreset.ELEMENT_DANGER)
 * private ButtonUIComponent deleteBtn;
 *
 * // 5. 图标按钮
 * @ButtonStyle(preset = ButtonPreset.ICON_BUTTON)
 * private ButtonUIComponent settingsBtn;
 *
 * // 6. 浮动操作按钮
 * @ButtonStyle(preset = ButtonPreset.FAB)
 * private ButtonUIComponent addBtn;
 *
 * // 7. 完全自定义样式 - 渐变背景
 * @ButtonStyle(
 *     font = @CSFont(color = "#fff", fontWeight = CSFontWeight.BOLD),
 *     layout = @CSLayout(padding = "12px 24px"),
 *     border = @CSBorder(
 *         backgroundColor = "#667eea",
 *         borderRadius = "20px",
 *         boxShadow = "0 4px 12px rgba(102, 126, 234, 0.4)"
 *     )
 * )
 * private ButtonUIComponent gradientBtn;
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ButtonStyle {

    /**
     * 按钮预设样式
     * 可选值: MATERIAL_CONTAINED, MATERIAL_OUTLINED, MATERIAL_TEXT,
     *        ANT_PRIMARY, ANT_DEFAULT, ANT_DASHED, ANT_LINK,
     *        ELEMENT_PRIMARY, ELEMENT_SUCCESS, ELEMENT_WARNING, ELEMENT_DANGER, ELEMENT_ROUND,
     *        BOOTSTRAP_PRIMARY, BOOTSTRAP_SECONDARY, BOOTSTRAP_OUTLINE_PRIMARY,
     *        ICON_BUTTON, FAB, FAB_MINI
     * 默认值: UNSET (不应用任何预设，使用JS组件默认样式)
     *
     * 【预设详细说明】详见 ButtonPreset.java
     */
    ButtonPreset preset() default ButtonPreset.UNSET;

    /**
     * 字体样式覆盖
     * 用于覆盖预设或默认的字体相关样式
     * 如: @CSFont(color = "#fff", fontSize = "16px", fontWeight = CSFontWeight.BOLD)
     *
     * 【常用覆盖】
     * - 修改文字颜色: @CSFont(color = "#ff4d4f")
     * - 增大字体: @CSFont(fontSize = "16px")
     * - 调整字重: @CSFont(fontWeight = CSFontWeight.BOLD)
     */
    CSFont font() default @CSFont;

    /**
     * 布局样式覆盖
     * 用于覆盖预设或默认的布局相关样式
     * 如: @CSLayout(width = "120px", margin = "10px 0")
     *
     * 【常用覆盖】
     * - 固定宽度: @CSLayout(width = "120px")
     * - 调整外边距: @CSLayout(margin = "10px 0")
     * - 调整内边距: @CSLayout(padding = "12px 24px")
     */
    CSLayout layout() default @CSLayout;

    /**
     * 边框样式覆盖
     * 用于覆盖预设或默认的边框相关样式
     * 如: @CSBorder(borderRadius = "20px", backgroundColor = "#ff4d4f")
     *
     * 【常用覆盖】
     * - 调整圆角: @CSBorder(borderRadius = "20px")
     * - 修改背景色: @CSBorder(backgroundColor = "#52c41a")
     * - 添加阴影: @CSBorder(boxShadow = "0 4px 12px rgba(0,0,0,0.15)")
     */
    CSBorder border() default @CSBorder;

    /**
     * Flex布局配置
     * 用于按钮内部的Flex布局（较少使用）
     * 如: @CSFlex(justifyContent = "center", gap = "8px")
     *
     * 【说明】
     * 按钮默认使用 inline-flex 布局，通常不需要额外配置
     * 仅在按钮内部需要复杂布局时使用
     */
    CSFlex flex() default @CSFlex;
}
