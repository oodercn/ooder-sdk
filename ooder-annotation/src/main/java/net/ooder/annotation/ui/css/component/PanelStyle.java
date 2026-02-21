package net.ooder.annotation.ui.css.component;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFlex;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.preset.PanelPreset;

import java.lang.annotation.*;

/**
 * Panel组件样式注解
 * 用于快速应用面板/容器预设样式，同时支持自定义覆盖
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性 (layout=@CSLayout(...), border=@CSBorder(...), flex=@CSFlex(...))
 * 3. 预设样式 (PanelPreset.XXX)
 * 4. JS组件默认样式 (Panel.js)
 *
 * 【零注解默认值】(当没有任何注解时，Panel.js提供的默认样式):
 * - display: "block"
 * - width: "100%"
 * - padding: "24px"
 * - backgroundColor: "#ffffff"
 * - border: "1px solid #f0f0f0"
 * - borderRadius: "8px"
 * - boxShadow: "0 1px 2px rgba(0,0,0,0.03)"
 *
 * 【预设样式对照表】:
 * ┌─────────────────────┬──────────┬─────────────┬────────────┬────────────────────────────────┐
 * │ 预设                │ 内边距   │ 圆角        │ 阴影       │ 特殊说明                       │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼────────────────────────────────┤
 * │ ANT_CARD            │ 24px     │ 8px         │ 轻微阴影   │ 标准卡片样式                   │
 * │                     │          │             │ 0 1px 2px  │                                │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼────────────────────────────────┤
 * │ ANT_CARD_HOVER      │ 24px     │ 8px         │ 悬浮阴影   │ 鼠标悬浮时阴影加深             │
 * │                     │          │             │ 0 4px 12px │                                │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼────────────────────────────────┤
 * │ MATERIAL_CARD       │ 16px     │ 4px         │ 轻微阴影   │ Material Design卡片            │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼────────────────────────────────┤
 * │ MATERIAL_ELEVATED   │ 16px     │ 4px         │ 明显阴影   │ Material Design凸起卡片        │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼────────────────────────────────┤
 * │ CARD                │ 20px     │ 8px         │ 中等阴影   │ 通用卡片样式                   │
 * ├─────────────────────┼──────────┼─────────────┼────────────┼────────────────────────────────┤
 * │ CARD_SHADOW         │ 24px     │ 12px        │ 强阴影     │ 大圆角强阴影卡片               │
 * └─────────────────────┴──────────┴─────────────┴────────────┴────────────────────────────────┘
 *
 * 【使用模式】:
 * 模式1 - 纯预设: @PanelStyle(preset = PanelPreset.ANT_CARD)
 * 模式2 - 覆盖预设: @PanelStyle(preset = PanelPreset.ANT_CARD, layout = @CSLayout(padding = "32px"))
 * 模式3 - Flex容器: @PanelStyle(flex = @CSFlex(justifyContent = "center", gap = "16px"))
 * 模式4 - 完全自定义: @PanelStyle(layout = @CSLayout(...), border = @CSBorder(...))
 *
 * 【Panel作为Flex容器】:
 * Panel组件通常作为布局容器使用，配合CSFlex实现复杂布局:
 *
 * // 水平居中按钮组
 * @PanelStyle(
 *     preset = PanelPreset.ANT_CARD,
 *     flex = @CSFlex(justifyContent = "center", gap = "16px")
 * )
 * private PanelUIComponent buttonGroup;
 *
 * // 垂直堆叠表单
 * @PanelStyle(
 *     preset = PanelPreset.ANT_CARD,
 *     flex = @CSFlex(flexDirection = "column", gap = "24px")
 * )
 * private PanelUIComponent formContainer;
 *
 * // 两端对齐头部
 * @PanelStyle(
 *     preset = PanelPreset.ANT_CARD,
 *     flex = @CSFlex(justifyContent = "space-between", alignItems = "center")
 * )
 * private PanelUIComponent headerPanel;
 *
 * 【与CardStyle的区别】:
 * ⚠️ 注意: PanelStyle和CardStyle是不同的注解
 *          PanelStyle用于通用容器，功能更全面
 *          CardStyle专用于卡片组件，语义更明确
 *          两者预设值类似，但适用场景不同
 *
 * 【嵌套使用】:
 * Panel可以嵌套使用，构建复杂布局:
 *
 * // 外层容器
 * @PanelStyle(
 *     preset = PanelPreset.ANT_CARD,
 *     layout = @CSLayout(padding = "0")
 * )
 * private PanelUIComponent outerPanel;
 *
 * // 内层头部
 * @PanelStyle(
 *     border = @CSBorder(borderBottom = "1px solid #f0f0f0"),
 *     layout = @CSLayout(padding = "16px 24px")
 * )
 * private PanelUIComponent cardHeader;
 *
 * // 内层内容
 * @PanelStyle(
 *     layout = @CSLayout(padding = "24px")
 * )
 * private PanelUIComponent cardBody;
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PanelStyle {

    /**
     * 面板预设样式
     * 可选值: ANT_CARD, ANT_CARD_HOVER, MATERIAL_CARD, MATERIAL_ELEVATED,
     *        CARD, CARD_SHADOW
     * 默认值: UNSET (不应用任何预设，使用JS组件默认样式)
     *
     * 【预设详细说明】
     * - ANT_CARD: Ant Design标准卡片，24px内边距，8px圆角，轻微阴影
     * - ANT_CARD_HOVER: 悬浮效果卡片，鼠标悬浮时阴影加深
     * - MATERIAL_CARD: Material Design标准卡片，16px内边距，4px圆角
     * - MATERIAL_ELEVATED: Material Design凸起卡片，更明显阴影
     * - CARD: 通用卡片样式，20px内边距，8px圆角，中等阴影
     * - CARD_SHADOW: 强阴影卡片，24px内边距，12px圆角，明显阴影效果
     */
    PanelPreset preset() default PanelPreset.UNSET;

    /**
     * 字体样式覆盖
     * 用于覆盖预设或默认的字体相关样式
     * 如: @CSFont(color = "#1677ff", fontSize = "16px")
     *
     * 【常用覆盖】
     * - 修改标题颜色: @CSFont(color = "#1677ff")
     * - 调整正文字号: @CSFont(fontSize = "14px")
     * - 设置行高: @CSFont(lineHeight = "1.6")
     */
    CSFont font() default @CSFont;

    /**
     * 布局样式覆盖
     * 用于覆盖预设或默认的布局相关样式
     * 如: @CSLayout(width = "400px", padding = "32px")
     *
     * 【常用覆盖】
     * - 固定宽度: @CSLayout(width = "400px")
     * - 占满容器: @CSLayout(width = "100%")
     * - 调整内边距: @CSLayout(padding = "32px")
     * - 设置最大宽度: @CSLayout(maxWidth = "800px")
     */
    CSLayout layout() default @CSLayout;

    /**
     * 边框样式覆盖
     * 用于覆盖预设或默认的边框相关样式
     * 如: @CSBorder(borderRadius = "12px", boxShadow = "0 4px 12px rgba(0,0,0,0.15)")
     *
     * 【常用覆盖】
     * - 调整圆角: @CSBorder(borderRadius = "12px")
     * - 修改阴影: @CSBorder(boxShadow = "0 8px 24px rgba(0,0,0,0.12)")
     * - 无边框: @CSBorder(border = "none")
     * - 背景色: @CSBorder(backgroundColor = "#f5f5f5")
     */
    CSBorder border() default @CSBorder;

    /**
     * Flex布局配置
     * 用于将Panel配置为Flex容器
     * 如: @CSFlex(justifyContent = "center", gap = "16px")
     *
     * 【常用配置】
     * - 水平居中: @CSFlex(justifyContent = "center")
     * - 两端对齐: @CSFlex(justifyContent = "space-between")
     * - 垂直堆叠: @CSFlex(flexDirection = "column", gap = "16px")
     * - 等分布局: @CSFlex(gap = "16px") + 子元素flex="1"
     *
     * 【重要】
     * 设置CSFlex后，Panel的display会自动设置为"flex"
     * 无需再通过CSLayout设置display
     */
    CSFlex flex() default @CSFlex;
}
