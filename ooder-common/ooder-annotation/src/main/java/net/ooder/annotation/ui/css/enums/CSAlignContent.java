package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS align-content 属性枚举
 * <p>
 * align-content 用于定义多行 Flex 容器在交叉轴方向上的行对齐方式。
 * 当 flex-wrap 设置为 wrap 或 wrap-reverse 且容器内存在多行内容时生效。
 * </p>
 *
 * <h3>取值对照表</h3>
 * <table border="1">
 *     <tr><th>枚举值</th><th>CSS 值</th><th>行为描述</th></tr>
 *     <tr><td>UNSET</td><td>(空字符串)</td><td>未设置，使用浏览器默认值</td></tr>
 *     <tr><td>STRETCH</td><td>stretch</td><td>默认值，行拉伸占满整个交叉轴空间</td></tr>
 *     <tr><td>FLEX_START</td><td>flex-start</td><td>行向交叉轴起点对齐</td></tr>
 *     <tr><td>FLEX_END</td><td>flex-end</td><td>行向交叉轴终点对齐</td></tr>
 *     <tr><td>CENTER</td><td>center</td><td>行在交叉轴居中对齐</td></tr>
 *     <tr><td>SPACE_BETWEEN</td><td>space-between</td><td>首行起点、末行终点对齐，剩余行均匀分布</td></tr>
 *     <tr><td>SPACE_AROUND</td><td>space-around</td><td>每行周围分配相等空间（首尾行空间为中间的一半）</td></tr>
 *     <tr><td>SPACE_EVENLY</td><td>space-evenly</td><td>所有行之间及首尾行与边界间距完全相等</td></tr>
 * </table>
 *
 * <h3>使用建议</h3>
 * <ul>
 *     <li>仅在多行 Flex 容器（flex-wrap: wrap）中有效，单行布局请使用 align-items</li>
 *     <li>需要显式设置容器高度，否则多行内容会撑满容器导致对齐效果不可见</li>
 *     <li>STRETCH 要求子元素未设置固定交叉轴尺寸（如未设置 height 当 flex-direction: row）</li>
 * </ul>
 *
 * <h3>常见组合</h3>
 * <ul>
 *     <li>水平垂直居中：justify-content: CENTER + align-content: CENTER（多行）</li>
 *     <li>卡片网格布局：flex-wrap: WRAP + align-content: FLEX_START + 固定容器高度</li>
 *     <li>底部对齐按钮组：align-content: FLEX_END 配合固定高度侧边栏</li>
 *     <li>瀑布流/标签云：flex-wrap: WRAP + align-content: SPACE_BETWEEN</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/align-content">MDN align-content</a>
 */
public enum CSAlignContent {

    /**
     * 未设置，使用浏览器默认值
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 默认值，行拉伸占满整个交叉轴空间
     */
    @JSONField(name = "stretch")
    STRETCH("stretch"),

    /**
     * 行向交叉轴起点对齐
     */
    @JSONField(name = "flex-start")
    FLEX_START("flex-start"),

    /**
     * 行向交叉轴终点对齐
     */
    @JSONField(name = "flex-end")
    FLEX_END("flex-end"),

    /**
     * 行在交叉轴居中对齐
     */
    @JSONField(name = "center")
    CENTER("center"),

    /**
     * 首行起点、末行终点对齐，剩余行均匀分布
     */
    @JSONField(name = "space-between")
    SPACE_BETWEEN("space-between"),

    /**
     * 每行周围分配相等空间（首尾行空间为中间的一半）
     */
    @JSONField(name = "space-around")
    SPACE_AROUND("space-around"),

    /**
     * 所有行之间及首尾行与边界间距完全相等
     */
    @JSONField(name = "space-evenly")
    SPACE_EVENLY("space-evenly");

    private final String value;

    CSAlignContent(String value) {
        this.value = value;
    }

    /**
     * 获取 CSS 属性值
     *
     * @return CSS 字符串值，如 "center"、"flex-start"
     */
    public String getValue() {
        return value;
    }

    /**
     * 返回 CSS 属性值字符串
     *
     * @return 与 getValue() 相同的结果
     */
    @Override
    public String toString() {
        return value;
    }
}
