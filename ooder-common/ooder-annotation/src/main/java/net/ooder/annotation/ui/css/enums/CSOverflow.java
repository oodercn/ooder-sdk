package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS overflow属性枚举
 * <p>
 * 属性作用：
 * overflow属性控制当元素内容超出其指定尺寸（宽/高）时的显示方式，
 * 决定溢出内容是被裁剪、显示滚动条还是直接显示在元素边界之外。
 * <p>
 * 取值对照表：
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS值</th><th>行为描述</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>未设置，继承父元素或使用浏览器默认值</td></tr>
 *   <tr><td>VISIBLE</td><td>"visible"</td><td>内容溢出时可见，不裁剪，不显示滚动条</td></tr>
 *   <tr><td>HIDDEN</td><td>"hidden"</td><td>内容溢出时被裁剪，不显示滚动条</td></tr>
 *   <tr><td>SCROLL</td><td>"scroll"</td><td>始终显示滚动条，无论内容是否溢出</td></tr>
 *   <tr><td>AUTO</td><td>"auto"</td><td>内容溢出时自动显示滚动条，否则不显示</td></tr>
 *   <tr><td>CLIP</td><td>"clip"</td><td>内容溢出时被裁剪，禁止任何形式的滚动</td></tr>
 * </table>
 * <p>
 * 使用建议：
 * <ul>
 *   <li>VISIBLE：适用于允许内容自然溢出的场景，如工具提示、下拉菜单</li>
 *   <li>HIDDEN：适用于固定尺寸且不允许滚动的容器，如图片裁剪框</li>
 *   <li>SCROLL：适用于需要保持滚动区域稳定的编辑器、日志显示区</li>
 *   <li>AUTO：适用于内容不确定的通用容器，如卡片、面板内容区</li>
 *   <li>CLIP：适用于严格限制显示区域的场景，如固定尺寸的预览窗口</li>
 * </ul>
 * <p>
 * 常见组合：
 * <ul>
 *   <li>overflow: hidden + text-overflow: ellipsis：文本溢出显示省略号</li>
 *   <li>overflow: auto + max-height：限制最大高度，超出时自动滚动</li>
 *   <li>overflow: scroll + white-space: nowrap：水平滚动表格</li>
 *   <li>overflow-x: hidden + overflow-y: auto：禁止水平滚动，允许垂直滚动</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/overflow">MDN overflow文档</a>
 */
public enum CSOverflow {

    /**
     * 未设置 - 继承父元素或使用浏览器默认值
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 可见 - 内容溢出时不裁剪，直接显示在元素边界外
     */
    @JSONField(name = "visible")
    VISIBLE("visible"),

    /**
     * 隐藏 - 内容溢出时被裁剪，不显示滚动条
     */
    @JSONField(name = "hidden")
    HIDDEN("hidden"),

    /**
     * 滚动 - 始终显示滚动条（即使内容未溢出）
     */
    @JSONField(name = "scroll")
    SCROLL("scroll"),

    /**
     * 自动 - 内容溢出时自动显示滚动条，否则不显示
     */
    @JSONField(name = "auto")
    AUTO("auto"),

    /**
     * 裁剪 - 内容溢出时被裁剪，禁止任何形式的滚动
     */
    @JSONField(name = "clip")
    CLIP("clip");

    private final String value;

    CSOverflow(String value) {
        this.value = value;
    }

    /**
     * 获取CSS属性值
     *
     * @return CSS字符串值
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
