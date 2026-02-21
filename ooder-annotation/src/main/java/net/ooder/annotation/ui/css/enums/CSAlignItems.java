package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS align-items 属性枚举
 * <p>
 * align-items 用于定义 flex 容器或 grid 容器中所有子元素在交叉轴（cross axis）上的对齐方式。
 * 交叉轴是与主轴（main axis）垂直的轴，方向由 flex-direction 决定。
 * </p>
 *
 * <h3>属性作用</h3>
 * <ul>
 *   <li>控制子元素在交叉轴方向上的对齐位置</li>
 *   <li>影响容器中所有直接子元素的默认对齐行为</li>
 *   <li>可被单个子元素的 align-self 属性覆盖</li>
 * </ul>
 *
 * <h3>取值对照表</h3>
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS 值</th><th>说明</th><th>适用场景</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>未设置，继承或默认</td><td>需要清除对齐设置时使用</td></tr>
 *   <tr><td>STRETCH</td><td>stretch</td><td>拉伸填充整个容器高度（默认值）</td><td>需要子元素等高布局</td></tr>
 *   <tr><td>FLEX_START</td><td>flex-start</td><td>对齐到交叉轴起点</td><td>内容顶部对齐</td></tr>
 *   <tr><td>FLEX_END</td><td>flex-end</td><td>对齐到交叉轴终点</td><td>内容底部对齐</td></tr>
 *   <tr><td>CENTER</td><td>center</td><td>居中对齐</td><td>垂直居中布局</td></tr>
 *   <tr><td>BASELINE</td><td>baseline</td><td>基线对齐</td><td>文本内容对齐</td></tr>
 *   <tr><td>START</td><td>start</td><td>对齐到书写模式起点</td><td>多语言布局</td></tr>
 *   <tr><td>END</td><td>end</td><td>对齐到书写模式终点</td><td>多语言布局</td></tr>
 *   <tr><td>SELF_START</td><td>self-start</td><td>根据自身对齐起点</td><td>复杂布局场景</td></tr>
 *   <tr><td>SELF_END</td><td>self-end</td><td>根据自身对齐终点</td><td>复杂布局场景</td></tr>
 * </table>
 *
 * <h3>对齐示意图（假设 flex-direction: row，交叉轴为垂直方向）</h3>
 * <pre>
 * ┌─────────────────────────────────────┐
 * │  FLEX_START    CENTER     FLEX_END  │
 * │  ┌─────┐      ┌─────┐     ┌─────┐  │
 * │  │  A  │      │  A  │     │  A  │  │
 * │  │  B  │      │  B  │     │  B  │  │
 * │  │  C  │      │  C  │     │  C  │  │
 * │  └─────┘      └─────┘     └─────┘  │
 * └─────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────┐
 * │            STRETCH (默认)            │
 * │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐  │
 * │  │     │ │     │ │     │ │     │  │
 * │  │  A  │ │  B  │ │  C  │ │  D  │  │
 * │  │     │ │     │ │     │ │     │  │
 * │  └─────┘ └─────┘ └─────┘ └─────┘  │
 * └─────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────┐
 * │           BASELINE                  │
 * │       ┌─────┐   ┌─────┐             │
 * │       │  A  │   │  B  │  基线对齐    │
 * │       │     │   │xxxxx│             │
 * │       └─────┘   └─────┘             │
 * └─────────────────────────────────────┘
 * </pre>
 *
 * <h3>使用建议</h3>
 * <ul>
 *   <li><b>垂直居中</b>：使用 CENTER，最常见的对齐需求</li>
 *   <li><b>等高卡片</b>：使用 STRETCH，让卡片自动填充高度</li>
 *   <li><b>顶部对齐</b>：使用 FLEX_START，保持内容顶部平齐</li>
 *   <li><b>底部对齐</b>：使用 FLEX_END，如按钮固定在底部</li>
 *   <li><b>文本混排</b>：使用 BASELINE，保持文字基线一致</li>
 * </ul>
 *
 * <h3>常见组合</h3>
 * <ul>
 *   <li>水平垂直居中：justify-content: CENTER + align-items: CENTER</li>
 *   <li>左侧垂直居中：justify-content: FLEX_START + align-items: CENTER</li>
 *   <li>底部右对齐：justify-content: FLEX_END + align-items: FLEX_END</li>
 *   <li>等高网格：align-items: STRETCH + 固定宽度子元素</li>
 *   <li>顶部导航栏：justify-content: SPACE_BETWEEN + align-items: CENTER</li>
 * </ul>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li>STRETCH 仅在子元素未设置固定高度时生效</li>
 *   <li>flex-start/flex-end 与 start/end 的区别在于是否考虑书写方向</li>
 *   <li>在 Grid 布局中同样适用</li>
 *   <li>如需单独控制某个子元素，使用 align-self 属性</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/align-items">MDN align-items</a>
 */
public enum CSAlignItems {

    /**
     * 未设置
     * <p>不指定 align-items 值，使用继承值或浏览器默认值</p>
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 拉伸
     * <p>默认值。子元素被拉伸以适应容器（在交叉轴方向填充可用空间）</p>
     * <p>注意：如果子元素设置了固定高度/宽度，则 stretch 不会生效</p>
     */
    @JSONField(name = "stretch")
    STRETCH("stretch"),

    /**
     * 交叉轴起点对齐
     * <p>子元素对齐到交叉轴的起点（flex 容器的顶部，当 flex-direction 为 row 时）</p>
     */
    @JSONField(name = "flex-start")
    FLEX_START("flex-start"),

    /**
     * 交叉轴终点对齐
     * <p>子元素对齐到交叉轴的终点（flex 容器的底部，当 flex-direction 为 row 时）</p>
     */
    @JSONField(name = "flex-end")
    FLEX_END("flex-end"),

    /**
     * 居中对齐
     * <p>子元素在交叉轴上居中对齐，最常用的对齐方式</p>
     */
    @JSONField(name = "center")
    CENTER("center"),

    /**
     * 基线对齐
     * <p>子元素根据文本基线对齐，适用于包含不同字号文本的布局</p>
     */
    @JSONField(name = "baseline")
    BASELINE("baseline"),

    /**
     * 书写模式起点对齐
     * <p>根据书写模式（writing-mode）对齐到交叉轴起点</p>
     * <p>与 flex-start 类似，但考虑文本方向（如从右到左的语言）</p>
     */
    @JSONField(name = "start")
    START("start"),

    /**
     * 书写模式终点对齐
     * <p>根据书写模式（writing-mode）对齐到交叉轴终点</p>
     * <p>与 flex-end 类似，但考虑文本方向（如从右到左的语言）</p>
     */
    @JSONField(name = "end")
    END("end"),

    /**
     * 自身起点对齐
     * <p>根据元素自身的对齐方式对齐到起点</p>
     * <p>考虑 align-self 和父元素的 align-items 设置</p>
     */
    @JSONField(name = "self-start")
    SELF_START("self-start"),

    /**
     * 自身终点对齐
     * <p>根据元素自身的对齐方式对齐到终点</p>
     * <p>考虑 align-self 和父元素的 align-items 设置</p>
     */
    @JSONField(name = "self-end")
    SELF_END("self-end");

    private final String value;

    CSAlignItems(String value) {
        this.value = value;
    }

    /**
     * 获取 CSS 属性值
     *
     * @return CSS 字符串值，如 "center"、"flex-start" 等
     */
    public String getValue() {
        return value;
    }

    /**
     * 返回 CSS 属性值字符串
     *
     * @return 与 getValue() 相同，CSS 字符串值
     */
    @Override
    public String toString() {
        return value;
    }
}
