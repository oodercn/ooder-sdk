package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS align-self属性枚举
 * <p>
 * align-self 用于定义单个Flex项目在交叉轴（cross axis）上的对齐方式。
 * 该属性可以覆盖容器的 align-items 属性，实现对特定项目的独立对齐控制。
 * </p>
 *
 * <h3>属性作用</h3>
 * <ul>
 *   <li>控制单个Flex项目在交叉轴上的对齐位置</li>
 *   <li>优先级高于容器的 align-items 设置</li>
 *   <li>适用于需要特殊对齐的个别项目</li>
 * </ul>
 *
 * <h3>取值对照表</h3>
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS值</th><th>说明</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>未设置，使用默认值</td></tr>
 *   <tr><td>AUTO</td><td>auto</td><td>继承容器的 align-items 值</td></tr>
 *   <tr><td>STRETCH</td><td>stretch</td><td>拉伸填满整个交叉轴（默认值）</td></tr>
 *   <tr><td>FLEX_START</td><td>flex-start</td><td>对齐到交叉轴起点</td></tr>
 *   <tr><td>FLEX_END</td><td>flex-end</td><td>对齐到交叉轴终点</td></tr>
 *   <tr><td>CENTER</td><td>center</td><td>居中对齐</td></tr>
 *   <tr><td>BASELINE</td><td>baseline</td><td>基线对齐</td></tr>
 * </table>
 *
 * <h3>与 align-items 的区别</h3>
 * <ul>
 *   <li>align-items: 作用于容器，控制所有子项目的对齐</li>
 *   <li>align-self: 作用于单个项目，覆盖 align-items 的设置</li>
 *   <li>align-self 的优先级高于 align-items</li>
 * </ul>
 *
 * <h3>使用建议</h3>
 * <ul>
 *   <li>当大部分项目需要对齐方式一致时，使用 align-items 统一设置</li>
 *   <li>当个别项目需要不同的对齐方式时，使用 align-self 单独设置</li>
 *   <li>使用 AUTO 让项目继承容器的对齐方式</li>
 *   <li>在垂直居中布局中，CENTER 是最常用的值</li>
 * </ul>
 *
 * <h3>常见组合</h3>
 * <ul>
 *   <li>侧栏导航: 主体内容 stretch，Logo 区域 flex-start</li>
 *   <li>卡片列表: 大部分卡片 stretch，特定卡片 center</li>
 *   <li>表单布局: 标签 flex-start，输入框 stretch</li>
 *   <li>按钮组: 主要按钮 stretch，图标按钮 center</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see CSAlignItems
 */
public enum CSAlignSelf {

    /**
     * 未设置
     * <p>值为空字符串，表示不应用该属性</p>
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 自动
     * <p>继承父容器的 align-items 值，如果没有父容器则表现为 stretch</p>
     */
    @JSONField(name = "auto")
    AUTO("auto"),

    /**
     * 拉伸
     * <p>项目被拉伸以填满整个交叉轴，同时遵守 min/max-width/height 限制</p>
     */
    @JSONField(name = "stretch")
    STRETCH("stretch"),

    /**
     * 起点对齐
     * <p>项目对齐到交叉轴的起点（交叉轴为垂直方向时是顶部，水平方向时是左侧）</p>
     */
    @JSONField(name = "flex-start")
    FLEX_START("flex-start"),

    /**
     * 终点对齐
     * <p>项目对齐到交叉轴的终点（交叉轴为垂直方向时是底部，水平方向时是右侧）</p>
     */
    @JSONField(name = "flex-end")
    FLEX_END("flex-end"),

    /**
     * 居中对齐
     * <p>项目在交叉轴上居中对齐，是最常用的对齐方式之一</p>
     */
    @JSONField(name = "center")
    CENTER("center"),

    /**
     * 基线对齐
     * <p>项目的第一行文字的基线对齐，适用于文本内容的对齐</p>
     */
    @JSONField(name = "baseline")
    BASELINE("baseline");

    private final String value;

    CSAlignSelf(String value) {
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
