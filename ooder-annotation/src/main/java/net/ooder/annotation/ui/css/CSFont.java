package net.ooder.annotation.ui.css;

import net.ooder.annotation.ui.css.enums.CSFontStyle;
import net.ooder.annotation.ui.css.enums.CSFontWeight;
import net.ooder.annotation.ui.css.enums.CSTextAlign;
import net.ooder.annotation.ui.css.enums.CSTextDecoration;
import net.ooder.annotation.ui.css.enums.CSTextTransform;
import net.ooder.annotation.ui.css.enums.CSVerticalAlign;
import net.ooder.annotation.ui.css.enums.CSWhiteSpace;
import net.ooder.annotation.ui.css.preset.ButtonPreset;
import net.ooder.annotation.ui.css.preset.InputPreset;

import java.lang.annotation.*;

/**
 * CSS字体样式注解
 * 用于定义文本相关的CSS属性
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性 (color="", fontSize=""等)
 * 3. 组件预设样式 (ButtonPreset.XXX.getFont())
 * 4. JS组件默认样式 (Button.js中的默认字体)
 *
 * 【零注解默认值】(当没有任何注解时，JS组件提供的默认样式):
 * - Button.js:  color="rgba(0,0,0,0.88)", fontSize="14px", fontWeight="400"
 * - Input.js:   color="rgba(0,0,0,0.88)", fontSize="14px"
 * - Panel.js:   继承父级字体
 *
 * 【使用模式】:
 * 模式1 - 纯预设: @CSFont → 使用预设字体样式
 * 模式2 - 覆盖预设: @CSFont(color="#fff") → 覆盖特定属性
 * 模式3 - 完全自定义: @CSFont(color="", fontSize="", ...) → 完全自定义
 *
 * 【属性冲突检测】:
 * ⚠️ 注意: buttonPreset和inputPreset不应同时使用
 * ⚠️ 注意: 空字符串""表示不设置，使用下一优先级的值
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSFont {

    /**
     * 按钮预设 - 用于快速应用按钮字体样式
     * 可选值: MATERIAL_CONTAINED, MATERIAL_OUTLINED, ANT_PRIMARY, ANT_DEFAULT等
     * 默认值: UNSET (不应用任何预设)
     *
     * 【覆盖关系】当preset!=UNSET时，预设的字体属性作为基础，
     * 本注解中显式设置的属性会覆盖预设中的对应属性
     */
    ButtonPreset buttonPreset() default ButtonPreset.UNSET;

    /**
     * 输入框预设 - 用于快速应用输入框字体样式
     * 可选值: ANT_DEFAULT, MATERIAL_STANDARD等
     * 默认值: UNSET (不应用任何预设)
     *
     * 【冲突提示】与buttonPreset不应同时使用
     */
    InputPreset inputPreset() default InputPreset.UNSET;

    /**
     * 文字颜色
     * 格式: CSS颜色值，如 "#1677ff", "rgba(0,0,0,0.88)", "red"
     * 默认值: "" (空字符串表示不设置，使用预设或JS默认值)
     *
     * 【JS默认值对照】
     * - Button.js: "rgba(0,0,0,0.88)" (默认) / "#ffffff" (主按钮)
     * - Input.js: "rgba(0,0,0,0.88)"
     * - Link.js: "#1677ff"
     */
    String color() default "";

    /**
     * 字体大小
     * 格式: CSS长度值，如 "14px", "1rem", "16px"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "14px"
     * - Input.js: "14px"
     * - 标题组件: "16px", "20px", "24px"等
     */
    String fontSize() default "";

    /**
     * 字体粗细
     * 可选值: NORMAL(400), BOLD(700), W500, W600, W700
     * 默认值: UNSET (不设置，使用预设或JS默认值)
     *
     * 【JS默认值对照】
     * - Button.js: NORMAL (400)
     * - 主按钮: W500 (500)
     * - 标题: BOLD (700)
     */
    CSFontWeight fontWeight() default CSFontWeight.UNSET;

    /**
     * 字体族
     * 格式: 字体名称，如 "Arial", "Microsoft YaHei", "sans-serif"
     * 默认值: "" (空字符串表示不设置，使用系统默认)
     *
     * 【JS默认值】
     * - 全局默认: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial
     */
    String fontFamily() default "";

    /**
     * 字体样式
     * 可选值: NORMAL, ITALIC, OBLIQUE
     * 默认值: UNSET
     */
    CSFontStyle fontStyle() default CSFontStyle.UNSET;

    /**
     * 行高
     * 格式: CSS长度值或倍数，如 "1.5", "24px", "150%"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "1.5714285714285714" (约1.57)
     * - Input.js: "1.5714285714285714"
     * - 文本段落: "1.5"
     */
    String lineHeight() default "";

    /**
     * 字间距
     * 格式: CSS长度值，如 "0.5px", "1px"
     * 默认值: "" (空字符串表示不设置)
     */
    String letterSpacing() default "";

    /**
     * 文本对齐
     * 可选值: LEFT, CENTER, RIGHT, JUSTIFY
     * 默认值: UNSET
     *
     * 【JS默认值】
     * - 按钮: CENTER
     * - 输入框: LEFT
     * - 表格单元格: 根据内容类型自动决定
     */
    CSTextAlign textAlign() default CSTextAlign.UNSET;

    /**
     * 文本装饰
     * 可选值: NONE, UNDERLINE, OVERLINE, LINE_THROUGH
     * 默认值: UNSET
     *
     * 【特殊用途】
     * - 链接默认: UNDERLINE
     * - 删除线: LINE_THROUGH
     */
    CSTextDecoration textDecoration() default CSTextDecoration.UNSET;

    /**
     * 文本转换
     * 可选值: NONE, CAPITALIZE, UPPERCASE, LOWERCASE
     * 默认值: UNSET
     */
    CSTextTransform textTransform() default CSTextTransform.UNSET;

    /**
     * 空白处理
     * 可选值: NORMAL, NOWRAP, PRE, PRE_LINE, PRE_WRAP
     * 默认值: UNSET
     *
     * 【特殊用途】
     * - 单行文本溢出: NOWRAP
     * - 保留格式文本: PRE
     */
    CSWhiteSpace whiteSpace() default CSWhiteSpace.UNSET;

    /**
     * 自动换行
     * 格式: CSS关键字，如 "break-word", "normal"
     * 默认值: "" (空字符串表示不设置)
     */
    String wordWrap() default "";

    /**
     * 文本溢出处理
     * 格式: CSS值，如 "ellipsis", "clip"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【常用组合】
     * whiteSpace=NOWRAP + textOverflow="ellipsis" + overflow=HIDDEN = 单行省略
     */
    String textOverflow() default "";

    /**
     * 文本阴影
     * 格式: CSS阴影值，如 "1px 1px 2px rgba(0,0,0,0.3)"
     * 默认值: "" (空字符串表示不设置)
     */
    String textShadow() default "";

    /**
     * 垂直对齐
     * 可选值: BASELINE, TOP, MIDDLE, BOTTOM, TEXT_TOP, TEXT_BOTTOM
     * 默认值: UNSET
     *
     * 【特殊用途】
     * - 图标与文字对齐: MIDDLE
     * - 表格单元格: 根据内容自动调整
     */
    CSVerticalAlign verticalAlign() default CSVerticalAlign.UNSET;
}
