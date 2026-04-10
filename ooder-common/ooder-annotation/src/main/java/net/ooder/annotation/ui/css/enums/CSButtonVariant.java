package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 按钮组件样式变体枚举
 * 
 * 【属性作用】
 * 定义按钮的视觉变体/语义类型，控制按钮的样式表现、颜色主题和尺寸规格。
 * 该枚举用于UI框架中按钮组件的样式配置，支持多种主流UI设计规范（Material Design, Ant Design, Element Plus）。
 * 
 * 【取值对照表】
 * | 枚举值      | JSON值      | 类别     | 说明                                      |
 * |-------------|-------------|----------|-------------------------------------------|
 * | UNSET       | ""          | 未设置   | 未指定样式，使用系统默认                   |
 * | DEFAULT     | "default"   | 样式类型 | 默认样式按钮                               |
 * | CONTAINED   | "contained" | 样式类型 | 实心填充按钮（Material Design风格）        |
 * | OUTLINED    | "outlined"  | 样式类型 | 边框按钮，透明背景带边框                   |
 * | TEXT        | "text"      | 样式类型 | 纯文本按钮，无背景无边框                   |
 * | PLAIN       | "plain"     | 样式类型 | 朴素按钮，简洁样式                         |
 * | DASHED      | "dashed"    | 样式类型 | 虚线边框按钮                               |
 * | LINK        | "link"      | 样式类型 | 链接样式按钮，类似超链接                   |
 * | GHOST       | "ghost"     | 样式类型 | 幽灵按钮，透明背景，用于深色背景           |
 * | ROUND       | "round"     | 形状     | 圆角按钮                                   |
 * | CIRCLE      | "circle"    | 形状     | 圆形按钮，通常用于图标按钮                 |
 * | PRIMARY     | "primary"   | 语义类型 | 主要按钮，品牌主色                         |
 * | SUCCESS     | "success"   | 语义类型 | 成功状态，绿色系                           |
 * | WARNING     | "warning"   | 语义类型 | 警告状态，黄色/橙色系                      |
 * | DANGER      | "danger"    | 语义类型 | 危险/删除操作，红色系                      |
 * | INFO        | "info"      | 语义类型 | 信息提示，蓝色系                           |
 * | LARGE       | "large"     | 尺寸     | 大尺寸按钮                                 |
 * | SMALL       | "small"     | 尺寸     | 小尺寸按钮                                 |
 * | MINI        | "mini"      | 尺寸     | 迷你尺寸按钮                               |
 * 
 * 【使用建议】
 * 1. 语义类型（PRIMARY/SUCCESS/WARNING/DANGER/INFO）用于表达按钮的操作含义
 * 2. 样式类型（CONTAINED/OUTLINED/TEXT/LINK）用于控制按钮的视觉层次
 * 3. 尺寸枚举（LARGE/SMALL/MINI）用于适配不同场景的空间需求
 * 4. UNSET 表示不指定变体，由组件内部决定默认表现
 * 
 * 【常见组合】
 * 1. 主要操作：PRIMARY + CONTAINED（实心主色按钮）
 * 2. 次要操作：DEFAULT + OUTLINED（边框默认按钮）
 * 3. 成功提交：SUCCESS + CONTAINED（实心绿色按钮）
 * 4. 删除确认：DANGER + CONTAINED（实心红色按钮）
 * 5. 取消/关闭：DEFAULT + TEXT（文本按钮）
 * 6. 外部链接：LINK（链接样式）
 * 7. 图标按钮：CIRCLE + SMALL/MINI（圆形小按钮）
 * 8. 表格操作：TEXT + PRIMARY/SUCCESS/DANGER（行内文字操作）
 * 
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSButtonVariant {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "contained")
    CONTAINED("contained"),

    @JSONField(name = "outlined")
    OUTLINED("outlined"),

    @JSONField(name = "text")
    TEXT("text"),

    @JSONField(name = "primary")
    PRIMARY("primary"),

    @JSONField(name = "dashed")
    DASHED("dashed"),

    @JSONField(name = "link")
    LINK("link"),

    @JSONField(name = "ghost")
    GHOST("ghost"),

    @JSONField(name = "default")
    DEFAULT("default"),

    @JSONField(name = "plain")
    PLAIN("plain"),

    @JSONField(name = "round")
    ROUND("round"),

    @JSONField(name = "circle")
    CIRCLE("circle"),

    @JSONField(name = "success")
    SUCCESS("success"),

    @JSONField(name = "warning")
    WARNING("warning"),

    @JSONField(name = "danger")
    DANGER("danger"),

    @JSONField(name = "info")
    INFO("info"),

    @JSONField(name = "large")
    LARGE("large"),

    @JSONField(name = "small")
    SMALL("small"),

    @JSONField(name = "mini")
    MINI("mini");

    private final String value;

    CSButtonVariant(String value) {
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
