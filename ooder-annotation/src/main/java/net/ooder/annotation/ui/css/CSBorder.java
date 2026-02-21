package net.ooder.annotation.ui.css;

import net.ooder.annotation.ui.css.enums.CSBorderStyle;
import net.ooder.annotation.ui.css.preset.ButtonPreset;
import net.ooder.annotation.ui.css.preset.InputPreset;

import java.lang.annotation.*;

/**
 * CSS边框与背景样式注解
 * 用于定义边框和背景相关的CSS属性
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性 (borderColor="", backgroundColor=""等)
 * 3. 组件预设样式 (ButtonPreset.XXX.getBorder())
 * 4. JS组件默认样式 (Button.js中的默认边框)
 *
 * 【零注解默认值】(当没有任何注解时，JS组件提供的默认样式):
 * - Button.js:  border="1px solid #d9d9d9", borderRadius="6px", backgroundColor="#ffffff"
 * - Input.js:   border="1px solid #d9d9d9", borderRadius="6px", backgroundColor="#ffffff"
 * - Panel.js:   border="1px solid #f0f0f0", borderRadius="8px", boxShadow="0 1px 2px rgba(0,0,0,0.03)"
 *
 * 【边框冲突检测】⚠️ 重要
 * 以下属性组合可能导致样式冲突，应避免同时使用:
 * - border + borderTop/Right/Bottom/Left 同时设置
 * - border + borderWidth/borderStyle/borderColor 同时设置
 * - 建议使用border简写，或分别设置四个边的边框
 *
 * 【背景冲突检测】⚠️ 重要
 * - background (简写) 与 backgroundColor 同时设置时，后设置的生效
 *
 * 【使用模式】:
 * 模式1 - 纯预设: @CSBorder → 使用预设边框样式
 * 模式2 - 覆盖预设: @CSBorder(borderRadius="8px") → 覆盖特定属性
 * 模式3 - 完全自定义: @CSBorder(borderColor="", backgroundColor="", ...) → 完全自定义
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSBorder {

    /**
     * 按钮预设 - 用于快速应用按钮边框样式
     * 可选值: MATERIAL_CONTAINED, MATERIAL_OUTLINED, ANT_PRIMARY, ANT_DEFAULT等
     * 默认值: UNSET (不应用任何预设)
     *
     * 【预设边框属性示例】
     * - ANT_PRIMARY: backgroundColor="#1677ff", borderColor="#1677ff", borderRadius="6px"
     * - ANT_DEFAULT: backgroundColor="#ffffff", borderColor="#d9d9d9", borderRadius="6px"
     * - ANT_DASHED: borderStyle="dashed"
     * - MATERIAL_CONTAINED: backgroundColor="#1976d2", border="none", borderRadius="4px"
     * - MATERIAL_OUTLINED: backgroundColor="transparent", borderColor="rgba(25,118,210,0.5)"
     * - FAB: backgroundColor="#1976d2", borderRadius="50%"
     * - ICON_BUTTON: backgroundColor="transparent", border="none", borderRadius="50%"
     */
    ButtonPreset buttonPreset() default ButtonPreset.UNSET;

    /**
     * 输入框预设 - 用于快速应用输入框边框样式
     * 可选值: ANT_DEFAULT, MATERIAL_STANDARD等
     * 默认值: UNSET
     *
     * 【预设边框属性示例】
     * - ANT_DEFAULT: border="1px solid #d9d9d9", borderRadius="6px", backgroundColor="#ffffff"
     * - ANT_BORDERLESS: border="none", backgroundColor="transparent"
     * - MATERIAL_STANDARD: borderBottom="1px solid rgba(0,0,0,0.42)", borderRadius="0"
     * - MATERIAL_FILLED: border="none", borderRadius="4px 4px 0 0", backgroundColor="#f5f5f5"
     * - MATERIAL_OUTLINED: border="1px solid rgba(0,0,0,0.23)", borderRadius="4px"
     */
    InputPreset inputPreset() default InputPreset.UNSET;

    /**
     * 边框简写
     * 格式: CSS边框简写值，如 "1px solid #d9d9d9", "2px dashed #ff4d4f"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "1px solid #d9d9d9" (默认) / "1px solid #1677ff" (主按钮)
     * - Input.js: "1px solid #d9d9d9"
     * - Panel.js: "1px solid #f0f0f0"
     * - 无边框: "none"
     *
     * ⚠️ 冲突提示: 与borderTop/Right/Bottom/Left或borderWidth/Style/Color同时设置可能导致冲突
     */
    String border() default "";

    /**
     * 边框宽度
     * 格式: CSS长度值，如 "1px", "2px", "0"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值】
     * - 大多数组件: "1px"
     * - 无边框: "0"
     */
    String borderWidth() default "";

    /**
     * 边框样式
     * 可选值: SOLID, DASHED, DOTTED, DOUBLE, GROOVE, RIDGE, INSET, OUTSET, NONE, HIDDEN
     * 默认值: UNSET
     *
     * 【JS默认值】
     * - 大多数组件: SOLID
     * - 虚线按钮: DASHED
     */
    CSBorderStyle borderStyle() default CSBorderStyle.UNSET;

    /**
     * 边框颜色
     * 格式: CSS颜色值，如 "#d9d9d9", "rgba(0,0,0,0.15)", "red"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "#d9d9d9" (默认) / "#1677ff" (主按钮)
     * - Input.js: "#d9d9d9" (默认) / "#1677ff" (聚焦)
     * - Panel.js: "#f0f0f0"
     * - 错误状态: "#ff4d4f"
     * - 警告状态: "#faad14"
     */
    String borderColor() default "";

    /**
     * 边框圆角
     * 格式: CSS长度值，如 "6px", "50%", "4px 8px"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "6px" (Ant Design) / "4px" (Material)
     * - Input.js: "6px" (Ant Design) / "0" (Material标准)
     * - Panel.js: "8px"
     * - Dialog.js: "8px"
     * - 圆形按钮: "50%"
     * - 圆角按钮(Element): "20px"
     */
    String borderRadius() default "";

    /**
     * 上边框
     * 格式: CSS边框简写值，如 "1px solid #d9d9d9"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - 卡片头部边框: 单独设置上边框
     * - 分割线效果: borderTop="1px solid #f0f0f0"
     */
    String borderTop() default "";

    /**
     * 右边框
     * 格式: CSS边框简写值，如 "1px solid #d9d9d9"
     * 默认值: "" (空字符串表示不设置)
     */
    String borderRight() default "";

    /**
     * 下边框
     * 格式: CSS边框简写值，如 "1px solid #d9d9d9"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - Material输入框: 仅保留下边框
     * - 分割线效果: borderBottom="1px solid #f0f0f0"
     */
    String borderBottom() default "";

    /**
     * 左边框
     * 格式: CSS边框简写值，如 "1px solid #d9d9d9"
     * 默认值: "" (空字符串表示不设置)
     */
    String borderLeft() default "";

    /**
     * 背景简写
     * 格式: CSS背景简写值，如 "#ffffff url(...) no-repeat center"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【建议】
     * 如需设置纯色背景，建议使用backgroundColor属性
     * 如需设置复杂背景，使用此简写属性
     */
    String background() default "";

    /**
     * 背景颜色
     * 格式: CSS颜色值，如 "#ffffff", "transparent", "rgba(0,0,0,0.5)"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "#ffffff" (默认) / "#1677ff" (主按钮) / "transparent" (链接)
     * - Input.js: "#ffffff"
     * - Panel.js: "#ffffff"
     * - 禁用状态: "#f5f5f5"
     * - 透明: "transparent"
     */
    String backgroundColor() default "";

    /**
     * 背景图片
     * 格式: CSS图片值，如 "url('image.png')", "linear-gradient(...)"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - 渐变背景: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
     * - 图标背景: "url('data:image/svg+xml,...')"
     */
    String backgroundImage() default "";

    /**
     * 背景尺寸
     * 格式: CSS值，如 "cover", "contain", "100% 100%", "auto"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【常用值】
     * - "cover": 等比缩放覆盖整个区域
     * - "contain": 等比缩放完整显示
     * - "100% 100%": 拉伸填充
     */
    String backgroundSize() default "";

    /**
     * 背景位置
     * 格式: CSS值，如 "center", "top left", "50% 50%"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【常用值】
     * - "center": 居中
     * - "top left": 左上角
     * - "right 10px bottom 10px": 距离右下10px
     */
    String backgroundPosition() default "";

    /**
     * 背景重复
     * 格式: CSS值，如 "no-repeat", "repeat", "repeat-x", "repeat-y"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值】
     * - 大多数场景: "no-repeat"
     * - 平铺背景: "repeat"
     */
    String backgroundRepeat() default "";

    /**
     * 阴影效果
     * 格式: CSS阴影值，如 "0 2px 8px rgba(0,0,0,0.15)"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Panel.js: "0 1px 2px rgba(0,0,0,0.03)" (轻微阴影)
     * - Card悬浮: "0 4px 12px rgba(0,0,0,0.15)"
     * - Dialog.js: "0 6px 16px rgba(0,0,0,0.08)"
     * - 按钮悬浮: "0 2px 0 rgba(0,0,0,0.045)"
     * - 无阴影: "none"
     *
     * 【阴影语法】
     * offset-x offset-y blur-radius spread-radius color
     * 如: "0 2px 8px 0 rgba(0,0,0,0.15)"
     */
    String boxShadow() default "";

    /**
     * 轮廓简写
     * 格式: CSS轮廓简写值，如 "2px solid #1677ff"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【与border的区别】
     * - outline不占据空间，不影响布局
     * - outline可以设置outline-offset
     * - 常用于: 聚焦状态的高亮边框
     */
    String outline() default "";

    /**
     * 轮廓颜色
     * 格式: CSS颜色值，如 "#1677ff", "rgba(0,0,0,0.2)"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值】
     * - 聚焦状态: "#1677ff" (主色)
     */
    String outlineColor() default "";

    /**
     * 轮廓宽度
     * 格式: CSS长度值，如 "2px", "3px"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值】
     * - 聚焦状态: "2px"
     */
    String outlineWidth() default "";

    /**
     * 轮廓样式
     * 可选值: SOLID, DASHED, DOTTED, DOUBLE等
     * 默认值: UNSET
     *
     * 【JS默认值】
     * - 聚焦状态: SOLID
     */
    CSBorderStyle outlineStyle() default CSBorderStyle.UNSET;
}
