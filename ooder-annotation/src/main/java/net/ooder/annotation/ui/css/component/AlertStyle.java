package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.AlertPreset;

import java.lang.annotation.*;

/**
 * Alert/Message组件样式注解
 * 用于快速应用警告/消息预设样式，同时支持自定义覆盖
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性 (font=@CSFont(...), layout=@CSLayout(...))
 * 3. 预设样式 (AlertPreset.XXX)
 * 4. JS组件默认样式 (Alert.js)
 *
 * 【零注解默认值】(当没有任何注解时，Alert.js提供的默认样式):
 * - display: "block"
 * - padding: "8px 15px"
 * - borderRadius: "6px"
 * - backgroundColor: "#e6f7ff" (INFO)
 * - border: "1px solid #91d5ff"
 * - color: "rgba(0,0,0,0.88)"
 * - fontSize: "14px"
 * - marginBottom: "16px"
 *
 * 【预设样式分类】:
 * 1. 颜色状态预设 (5种):
 *    - SUCCESS: 成功状态，绿色主题
 *    - INFO: 信息状态，蓝色主题 (默认)
 *    - WARNING: 警告状态，橙色主题
 *    - ERROR: 错误状态，红色主题
 *    - NEUTRAL: 中性状态，灰色主题
 *
 * 2. 样式变体预设 (3种):
 *    - FILLED: 填充样式，背景色较深
 *    - OUTLINED: 描边样式，仅边框着色
 *    - SUBTLE: 微妙样式，浅色背景
 *
 * 3. Material Design预设 (2种):
 *    - MATERIAL_SNACKBAR: 底部提示条
 *    - MATERIAL_BANNER: 顶部横幅
 *
 * 4. 特殊预设 (2种):
 *    - TOAST: 轻提示，自动消失
 *    - NOTIFICATION: 通知卡片，带操作按钮
 *
 * 【预设颜色对照表】:
 * ┌───────────┬──────────────┬──────────────┬──────────────┐
 * │ 预设      │ 背景色       │ 边框色       │ 文字色       │
 * ├───────────┼──────────────┼──────────────┼──────────────┤
 * │ SUCCESS   │ #f6ffed      │ #b7eb8f      │ #52c41a      │
 * │ INFO      │ #e6f7ff      │ #91d5ff      │ #1677ff      │
 * │ WARNING   │ #fffbe6      │ #ffe58f      │ #faad14      │
 * │ ERROR     │ #fff2f0      │ #ffccc7      │ #ff4d4f      │
 * │ NEUTRAL   │ #f5f5f5      │ #d9d9d9      │ #666666      │
 * └───────────┴──────────────┴──────────────┴──────────────┘
 *
 * 【使用模式】:
 * 模式1 - 纯预设: @AlertStyle(preset = AlertPreset.SUCCESS)
 * 模式2 - 覆盖预设: @AlertStyle(
 *             preset = AlertPreset.INFO,
 *             layout = @CSLayout(margin = "20px")
 *         )
 * 模式3 - 完全自定义: @AlertStyle(
 *             font = @CSFont(color = "#fff"),
 *             border = @CSBorder(backgroundColor = "#722ed1")
 *         )
 *
 * 【使用建议】:
 * 1. 操作成功: 使用 SUCCESS
 * 2. 信息提示: 使用 INFO
 * 3. 警告提醒: 使用 WARNING
 * 4. 错误提示: 使用 ERROR
 * 5. 底部提示: 使用 MATERIAL_SNACKBAR
 * 6. 轻量提示: 使用 TOAST
 *
 * 【与ButtonStyle的区别】:
 * ⚠️ 注意: AlertStyle用于消息提示，ButtonStyle用于操作按钮
 *          两者预设值和默认样式不同
 *
 * 使用示例：
 * <pre>
 * // 1. 成功提示
 * @AlertStyle(preset = AlertPreset.SUCCESS)
 * private AlertUIComponent successAlert;
 *
 * // 2. 错误提示
 * @AlertStyle(preset = AlertPreset.ERROR)
 * private AlertUIComponent errorAlert;
 *
 * // 3. 描边样式警告
 * @AlertStyle(preset = AlertPreset.WARNING)
 * private AlertUIComponent warningAlert;
 *
 * // 4. Material Snackbar
 * @AlertStyle(preset = AlertPreset.MATERIAL_SNACKBAR)
 * private AlertUIComponent snackbar;
 *
 * // 5. Toast通知
 * @AlertStyle(preset = AlertPreset.TOAST)
 * private AlertUIComponent toast;
 *
 * // 6. 自定义覆盖
 * @AlertStyle(
 *     preset = AlertPreset.INFO,
 *     layout = @CSLayout(margin = "20px")
 * )
 * private AlertUIComponent customAlert;
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AlertStyle {

    /**
     * Alert预设样式
     * 可选值: SUCCESS, INFO, WARNING, ERROR, NEUTRAL,
     *        FILLED, OUTLINED, SUBTLE,
     *        MATERIAL_SNACKBAR, MATERIAL_BANNER,
     *        TOAST, NOTIFICATION
     * 默认值: UNSET (不应用任何预设，使用JS组件默认样式)
     *
     * 【预设详细说明】详见 AlertPreset.java
     */
    AlertPreset preset() default AlertPreset.UNSET;

    /**
     * 字体样式覆盖
     * 用于覆盖预设或默认的字体相关样式
     * 如: @CSFont(color = "#ff4d4f", fontSize = "14px")
     *
     * 【常用覆盖】
     * - 修改文字颜色: @CSFont(color = "#ff4d4f")
     * - 调整字号: @CSFont(fontSize = "16px")
     * - 加粗文字: @CSFont(fontWeight = CSFontWeight.BOLD)
     */
    CSFont font() default @CSFont;

    /**
     * 布局样式覆盖
     * 用于覆盖预设或默认的布局相关样式
     * 如: @CSLayout(margin = "20px", padding = "16px")
     *
     * 【常用覆盖】
     * - 调整外边距: @CSLayout(margin = "20px 0")
     * - 调整内边距: @CSLayout(padding = "16px 24px")
     * - 固定宽度: @CSLayout(width = "400px")
     */
    CSLayout layout() default @CSLayout;

    /**
     * 边框样式覆盖
     * 用于覆盖预设或默认的边框相关样式
     * 如: @CSBorder(borderRadius = "8px", backgroundColor = "#fff2f0")
     *
     * 【常用覆盖】
     * - 调整圆角: @CSBorder(borderRadius = "8px")
     * - 修改背景色: @CSBorder(backgroundColor = "#f6ffed")
     * - 修改边框色: @CSBorder(borderColor = "#b7eb8f")
     */
    CSBorder border() default @CSBorder;
}
