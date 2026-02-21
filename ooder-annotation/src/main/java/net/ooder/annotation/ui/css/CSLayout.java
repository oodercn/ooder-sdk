package net.ooder.annotation.ui.css;

import net.ooder.annotation.ui.css.enums.CSBoxSizing;
import net.ooder.annotation.ui.css.enums.CSCursor;
import net.ooder.annotation.ui.css.enums.CSDisplay;
import net.ooder.annotation.ui.css.enums.CSOverflow;
import net.ooder.annotation.ui.css.enums.CSPosition;
import net.ooder.annotation.ui.css.enums.CSVisibility;
import net.ooder.annotation.ui.css.preset.ButtonPreset;
import net.ooder.annotation.ui.css.preset.InputPreset;

import java.lang.annotation.*;

/**
 * CSS布局样式注解
 * 用于定义布局相关的CSS属性
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性 (width="", height=""等)
 * 3. 组件预设样式 (ButtonPreset.XXX.getLayout())
 * 4. JS组件默认样式 (Button.js中的默认布局)
 *
 * 【零注解默认值】(当没有任何注解时，JS组件提供的默认样式):
 * - Button.js:  display="inline-flex", height="32px", padding="4px 15px"
 * - Input.js:   height="32px", padding="4px 11px"
 * - Panel.js:   display="block", padding="24px"
 *
 * 【尺寸冲突检测】⚠️ 重要
 * 以下属性组合可能导致样式冲突，应避免同时使用:
 * - width + minWidth/maxWidth 同时设置
 * - height + minHeight/maxHeight 同时设置
 * - padding + paddingLeft/Right/Top/Bottom 同时设置
 * - margin + marginLeft/Right/Top/Bottom 同时设置
 *
 * 【使用模式】:
 * 模式1 - 纯预设: @CSLayout → 使用预设布局样式
 * 模式2 - 覆盖预设: @CSLayout(width="120px") → 覆盖特定属性
 * 模式3 - 完全自定义: @CSLayout(width="", height="", ...) → 完全自定义
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSLayout {

    /**
     * 按钮预设 - 用于快速应用按钮布局样式
     * 可选值: MATERIAL_CONTAINED, MATERIAL_OUTLINED, ANT_PRIMARY, ANT_DEFAULT等
     * 默认值: UNSET (不应用任何预设)
     *
     * 【预设布局属性示例】
     * - ANT_PRIMARY: height="", padding="6px 16px"
     * - MATERIAL_CONTAINED: padding="8px 22px"
     * - FAB: width="56px", height="56px"
     * - ICON_BUTTON: width="40px", height="40px"
     */
    ButtonPreset buttonPreset() default ButtonPreset.UNSET;

    /**
     * 输入框预设 - 用于快速应用输入框布局样式
     * 可选值: ANT_DEFAULT, MATERIAL_STANDARD等
     * 默认值: UNSET
     *
     * 【预设布局属性示例】
     * - ANT_DEFAULT: height="32px", padding="4px 11px"
     * - MATERIAL_STANDARD: height="48px", padding="12px 0"
     */
    InputPreset inputPreset() default InputPreset.UNSET;

    /**
     * 显示类型
     * 可选值: BLOCK, INLINE, INLINE_BLOCK, FLEX, INLINE_FLEX, GRID, NONE
     * 默认值: UNSET
     *
     * 【JS默认值对照】
     * - Button.js: "inline-flex"
     * - Input.js: "inline-block"
     * - Panel.js: "block"
     * - Flex容器: "flex"
     */
    CSDisplay display() default CSDisplay.UNSET;

    /**
     * 定位类型
     * 可选值: STATIC, RELATIVE, ABSOLUTE, FIXED, STICKY
     * 默认值: UNSET
     *
     * 【JS默认值】
     * - 大多数组件: "relative"
     * - 绝对定位元素: "absolute"
     * - 固定定位: "fixed"
     */
    CSPosition position() default CSPosition.UNSET;

    /**
     * 顶部偏移 (配合position使用)
     * 格式: CSS长度值，如 "10px", "5%", "0"
     * 默认值: "" (空字符串表示不设置)
     */
    String top() default "";

    /**
     * 左侧偏移 (配合position使用)
     * 格式: CSS长度值，如 "10px", "5%"
     * 默认值: "" (空字符串表示不设置)
     */
    String left() default "";

    /**
     * 右侧偏移 (配合position使用)
     * 格式: CSS长度值，如 "10px", "5%"
     * 默认值: "" (空字符串表示不设置)
     */
    String right() default "";

    /**
     * 底部偏移 (配合position使用)
     * 格式: CSS长度值，如 "10px", "5%"
     * 默认值: "" (空字符串表示不设置)
     */
    String bottom() default "";

    /**
     * 宽度
     * 格式: CSS长度值，如 "100%", "120px", "auto", "fit-content"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "auto" (根据内容自适应)
     * - Input.js: "auto" / 可设置具体值如 "200px"
     * - Panel.js: "100%" (默认占满父容器)
     * - Dialog.js: "520px" (固定宽度)
     *
     * ⚠️ 冲突提示: 与minWidth/maxWidth同时设置可能导致意外行为
     */
    String width() default "";

    /**
     * 高度
     * 格式: CSS长度值，如 "40px", "100%", "auto"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "32px" (标准按钮高度)
     * - Input.js: "32px" (标准输入框高度)
     * - Panel.js: "auto" (根据内容自适应)
     * - TextArea.js: "auto" / 可设置如 "100px"
     *
     * ⚠️ 冲突提示: 与minHeight/maxHeight同时设置可能导致意外行为
     */
    String height() default "";

    /**
     * 最小宽度
     * 格式: CSS长度值，如 "100px", "50%"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - 按钮: 确保按钮有最小可点击区域
     * - 输入框: 确保最小可输入宽度
     */
    String minWidth() default "";

    /**
     * 最大宽度
     * 格式: CSS长度值，如 "500px", "100%"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - 文本容器: 限制最大阅读宽度 (如 "800px")
     * - 响应式布局: 配合width:100%使用
     */
    String maxWidth() default "";

    /**
     * 最小高度
     * 格式: CSS长度值，如 "40px", "100%"
     * 默认值: "" (空字符串表示不设置)
     */
    String minHeight() default "";

    /**
     * 最大高度
     * 格式: CSS长度值，如 "500px", "100vh"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - 滚动区域: 限制最大高度并配合overflow:auto
     * - 模态框: 限制最大高度避免超出视口
     */
    String maxHeight() default "";

    /**
     * 内边距 (简写)
     * 格式: CSS长度值，如 "10px", "6px 16px" (上下 左右), "10px 20px 30px 40px" (上右下左)
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值对照】
     * - Button.js: "4px 15px" (标准) / "6px 16px" (Ant Design)
     * - Input.js: "4px 11px"
     * - Panel.js: "24px" (卡片内边距)
     * - Table单元格: "16px"
     *
     * ⚠️ 冲突提示: 与paddingLeft/Right/Top/Bottom同时设置可能导致冲突
     */
    String padding() default "";

    /**
     * 左内边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String paddingLeft() default "";

    /**
     * 右内边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String paddingRight() default "";

    /**
     * 上内边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String paddingTop() default "";

    /**
     * 下内边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String paddingBottom() default "";

    /**
     * 外边距 (简写)
     * 格式: CSS长度值，如 "10px", "10px 20px", "0 auto" (水平居中)
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值】
     * - 大多数组件: "0"
     * - 表单元素间距: 通常通过父容器gap或单独设置marginBottom
     *
     * ⚠️ 冲突提示: 与marginLeft/Right/Top/Bottom同时设置可能导致冲突
     */
    String margin() default "";

    /**
     * 左外边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String marginLeft() default "";

    /**
     * 右外边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String marginRight() default "";

    /**
     * 上外边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String marginTop() default "";

    /**
     * 下外边距
     * 格式: CSS长度值，如 "10px"
     * 默认值: "" (空字符串表示不设置)
     */
    String marginBottom() default "";

    /**
     * 溢出处理
     * 可选值: VISIBLE, HIDDEN, SCROLL, AUTO
     * 默认值: UNSET
     *
     * 【JS默认值】
     * - 大多数组件: VISIBLE
     * - 表格容器: AUTO (允许滚动)
     * - 文本省略容器: HIDDEN
     */
    CSOverflow overflow() default CSOverflow.UNSET;

    /**
     * X轴溢出处理
     * 可选值: VISIBLE, HIDDEN, SCROLL, AUTO
     * 默认值: UNSET
     */
    CSOverflow overflowX() default CSOverflow.UNSET;

    /**
     * Y轴溢出处理
     * 可选值: VISIBLE, HIDDEN, SCROLL, AUTO
     * 默认值: UNSET
     *
     * 【特殊用途】
     * - 固定高度容器: AUTO (内容超出时显示滚动条)
     * - 模态框内容区: AUTO
     */
    CSOverflow overflowY() default CSOverflow.UNSET;

    /**
     * 层级 (z-index)
     * 格式: 整数值，如 "100", "999", "1000"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值】
     * - 普通元素: "auto"
     * - 下拉菜单: "1000"
     * - 模态框遮罩: "1000"
     * - 模态框内容: "1001"
     * - 通知消息: "1010"
     */
    String zIndex() default "";

    /**
     * 浮动
     * 格式: CSS值，如 "left", "right", "none"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【现代替代方案】
     * 建议使用Flexbox或Grid布局替代float
     */
    String floatValue() default "";

    /**
     * 清除浮动
     * 格式: CSS值，如 "both", "left", "right"
     * 默认值: "" (空字符串表示不设置)
     */
    String clear() default "";

    /**
     * 可见性
     * 可选值: VISIBLE, HIDDEN, COLLAPSE
     * 默认值: UNSET
     *
     * 【与display:none的区别】
     * - VISIBLE/HIDDEN: 元素仍占据空间，只是不可见
     * - display:none: 元素不占据空间
     */
    CSVisibility visibility() default CSVisibility.UNSET;

    /**
     * 透明度
     * 格式: 0-1之间的小数，如 "0.5", "1", "0.8"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【JS默认值】
     * - 正常元素: "1"
     * - 禁用状态: "0.6" 或 "0.4"
     */
    String opacity() default "";

    /**
     * 鼠标光标样式
     * 可选值: DEFAULT, POINTER, TEXT, MOVE, NOT_ALLOWED, WAIT等
     * 默认值: UNSET
     *
     * 【JS默认值对照】
     * - Button.js: POINTER (手型)
     * - Input.js: TEXT (文本输入光标)
     * - 禁用元素: NOT_ALLOWED (禁止符号)
     * - 可拖拽元素: MOVE
     */
    CSCursor cursor() default CSCursor.UNSET;

    /**
     * 指针事件
     * 格式: CSS值，如 "none", "auto"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - "none": 元素不响应鼠标事件，事件穿透到下层元素
     * - 常用于: 遮罩层下的点击穿透
     */
    String pointerEvents() default "";

    /**
     * 用户选择
     * 格式: CSS值，如 "none", "text", "all"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【特殊用途】
     * - "none": 禁止用户选择文本 (用于按钮、图标)
     * - "text": 允许选择文本
     */
    String userSelect() default "";

    /**
     * 盒模型计算方式
     * 可选值: CONTENT_BOX, BORDER_BOX
     * 默认值: UNSET
     *
     * 【说明】
     * - CONTENT_BOX: width/height只包含内容
     * - BORDER_BOX: width/height包含内容+内边距+边框 (推荐)
     *
     * 【JS默认值】
     * - 现代UI框架通常默认使用BORDER_BOX
     */
    CSBoxSizing boxSizing() default CSBoxSizing.UNSET;
}
