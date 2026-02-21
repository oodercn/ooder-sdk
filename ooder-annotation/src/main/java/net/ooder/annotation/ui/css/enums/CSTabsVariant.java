package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Tabs组件样式变体枚举
 * 定义标签页组件的视觉样式、布局方向和交互特性
 * 
 * 【属性作用】
 * 该枚举用于控制标签页组件的整体外观表现，包括：
 * - 视觉风格：标签的外观样式（线条、卡片、轮廓等）
 * - 布局方向：标签的排列位置（顶部、底部、左侧、右侧、垂直）
 * - 交互特性：是否支持编辑、拖拽、滚动等
 * - 尺寸规格：标签的大小（大、小、迷你）
 * - 布局模式：宽度适配（全宽、居中）
 * 
 * 【取值对照表】
 * | 枚举值       | JSON值        | 类别       | 说明                          |
 * |--------------|---------------|------------|-------------------------------|
 * | UNSET        | ""            | 未设置     | 不指定样式，使用系统默认      |
 * | DEFAULT      | "default"     | 视觉风格   | 默认样式，基础标签页外观      |
 * | OUTLINED     | "outlined"    | 视觉风格   | 轮廓样式，带边框线            |
 * | CONTAINED    | "contained"   | 视觉风格   | 填充样式，背景色填充          |
 * | PILLS        | "pills"       | 视觉风格   | 胶囊样式，圆角标签            |
 * | TOP          | "top"         | 布局方向   | 标签位于内容区顶部（默认）    |
 * | BOTTOM       | "bottom"      | 布局方向   | 标签位于内容区底部            |
 * | LEFT         | "left"        | 布局方向   | 标签位于内容区左侧            |
 * | RIGHT        | "right"       | 布局方向   | 标签位于内容区右侧            |
 * | LINE         | "line"        | 视觉风格   | 线条样式，下划线指示当前项    |
 * | CARD         | "card"        | 视觉风格   | 卡片样式，标签呈卡片状        |
 * | EDITABLE     | "editable"    | 交互特性   | 可编辑模式，支持增删标签      |
 * | DRAGGABLE    | "draggable"   | 交互特性   | 可拖拽模式，支持排序标签      |
 * | SCROLLABLE   | "scrollable"  | 交互特性   | 可滚动模式，超出时显示滚动条  |
 * | CENTERED     | "centered"    | 布局模式   | 居中对齐，标签在容器中居中    |
 * | FULL_WIDTH   | "full-width"  | 布局模式   | 全宽模式，标签平均分配宽度    |
 * | LARGE        | "large"       | 尺寸规格   | 大尺寸标签                    |
 * | SMALL        | "small"       | 尺寸规格   | 小尺寸标签                    |
 * | MINI         | "mini"        | 尺寸规格   | 迷你尺寸标签                  |
 * | SEGMENTED    | "segmented"   | 视觉风格   | 分段控制器样式                |
 * | RADIO        | "radio"       | 视觉风格   | 单选按钮组样式                |
 * | BUTTON       | "button"      | 视觉风格   | 按钮组样式                    |
 * | VERTICAL     | "vertical"    | 布局方向   | 垂直排列标签                  |
 * | BORDERED     | "bordered"    | 视觉风格   | 带边框样式                    |
 * 
 * 【使用建议】
 * 1. 视觉风格类（DEFAULT, LINE, CARD, PILLS, OUTLINED等）通常互斥，选择一种即可
 * 2. 布局方向类（TOP, BOTTOM, LEFT, RIGHT, VERTICAL）控制标签位置，根据页面空间选择
 * 3. 交互特性类（EDITABLE, DRAGGABLE, SCROLLABLE）可组合使用，用逗号分隔多个值
 * 4. 尺寸规格类（LARGE, SMALL, MINI）根据内容重要程度和空间选择
 * 5. 布局模式类（CENTERED, FULL_WIDTH）控制标签在容器中的分布方式
 * 
 * 【常见组合】
 * - 基础标签页：DEFAULT + TOP
 * - 卡片式标签：CARD + TOP
 * - 左侧导航：LEFT + LINE
 * - 胶囊按钮：PILLS + CENTERED
 * - 可编辑标签：DEFAULT + EDITABLE + SCROLLABLE
 * - 底部标签栏：BOTTOM + FULL_WIDTH
 * - 垂直侧边栏：VERTICAL + LEFT + LINE
 * - 分段控制器：SEGMENTED + FULL_WIDTH
 * - 移动端底部导航：BOTTOM + FULL_WIDTH + LARGE
 * 
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSTabsVariant {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "default")
    DEFAULT("default"),

    @JSONField(name = "outlined")
    OUTLINED("outlined"),

    @JSONField(name = "contained")
    CONTAINED("contained"),

    @JSONField(name = "pills")
    PILLS("pills"),

    @JSONField(name = "top")
    TOP("top"),

    @JSONField(name = "bottom")
    BOTTOM("bottom"),

    @JSONField(name = "left")
    LEFT("left"),

    @JSONField(name = "right")
    RIGHT("right"),

    @JSONField(name = "line")
    LINE("line"),

    @JSONField(name = "card")
    CARD("card"),

    @JSONField(name = "editable")
    EDITABLE("editable"),

    @JSONField(name = "draggable")
    DRAGGABLE("draggable"),

    @JSONField(name = "scrollable")
    SCROLLABLE("scrollable"),

    @JSONField(name = "centered")
    CENTERED("centered"),

    @JSONField(name = "full-width")
    FULL_WIDTH("full-width"),

    @JSONField(name = "large")
    LARGE("large"),

    @JSONField(name = "small")
    SMALL("small"),

    @JSONField(name = "mini")
    MINI("mini"),

    @JSONField(name = "segmented")
    SEGMENTED("segmented"),

    @JSONField(name = "radio")
    RADIO("radio"),

    @JSONField(name = "button")
    BUTTON("button"),

    @JSONField(name = "vertical")
    VERTICAL("vertical"),

    @JSONField(name = "bordered")
    BORDERED("bordered");

    private final String value;

    CSTabsVariant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
