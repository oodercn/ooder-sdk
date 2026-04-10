package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS justify-content属性枚举
 * <p>
 * 定义Flexbox或Grid容器中子元素在主轴(main axis)上的对齐方式。
 * 主轴方向由flex-direction决定（row为水平方向，column为垂直方向）。
 *
 * <h2>取值对照表</h2>
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS值</th><th>作用描述</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>未设置，使用浏览器默认值</td></tr>
 *   <tr><td>FLEX_START</td><td>flex-start</td><td>子元素向主轴起点对齐</td></tr>
 *   <tr><td>FLEX_END</td><td>flex-end</td><td>子元素向主轴终点对齐</td></tr>
 *   <tr><td>CENTER</td><td>center</td><td>子元素在主轴上居中对齐</td></tr>
 *   <tr><td>SPACE_BETWEEN</td><td>space-between</td><td>首尾贴边，中间元素均匀分布</td></tr>
 *   <tr><td>SPACE_AROUND</td><td>space-around</td><td>每个元素周围分配相同空间</td></tr>
 *   <tr><td>SPACE_EVENLY</td><td>space-evenly</td><td>所有间隔（包括首尾）完全相等</td></tr>
 *   <tr><td>START</td><td>start</td><td>向书写模式起点对齐（逻辑属性）</td></tr>
 *   <tr><td>END</td><td>end</td><td>向书写模式终点对齐（逻辑属性）</td></tr>
 *   <tr><td>LEFT</td><td>left</td><td>向左对齐（水平主轴时）</td></tr>
 *   <tr><td>RIGHT</td><td>right</td><td>向右对齐（水平主轴时）</td></tr>
 * </table>
 *
 * <h2>对齐示意图（水平主轴 row）</h2>
 * <pre>
 * FLEX_START:     [A][B][C]□□□□□□
 * FLEX_END:       □□□□□□[A][B][C]
 * CENTER:         □□□[A][B][C]□□□
 * SPACE_BETWEEN:  [A]□□□[B]□□□[C]
 * SPACE_AROUND:   □[A]□□[B]□□[C]□
 * SPACE_EVENLY:   □[A]□[B]□[C]□□
 * </pre>
 *
 * <h2>使用建议</h2>
 * <ul>
 *   <li><b>页面居中布局</b>：使用 CENTER 实现水平/垂直居中</li>
 *   <li><b>导航栏分布</b>：使用 SPACE_BETWEEN 让首尾菜单贴边</li>
 *   <li><b>按钮组等间距</b>：使用 SPACE_AROUND 或 SPACE_EVENLY</li>
 *   <li><b>表单底部按钮</b>：使用 FLEX_END 将按钮靠右对齐</li>
 *   <li><b>国际化项目</b>：使用 START/END 替代 LEFT/RIGHT，适配RTL语言</li>
 * </ul>
 *
 * <h2>常见组合</h2>
 * <ul>
 *   <li>水平垂直居中：justify-content: CENTER + align-items: CENTER</li>
 *   <li>底部固定按钮：justify-content: FLEX_END / SPACE_BETWEEN</li>
 *   <li>卡片列表：justify-content: SPACE_BETWEEN（两端对齐）</li>
 *   <li>工具栏：justify-content: SPACE_AROUND（图标均匀分布）</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/justify-content">MDN justify-content</a>
 */
public enum CSJustifyContent {

    /**
     * 未设置，使用浏览器默认值
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 向主轴起点对齐（默认行为）
     */
    @JSONField(name = "flex-start")
    FLEX_START("flex-start"),

    /**
     * 向主轴终点对齐
     */
    @JSONField(name = "flex-end")
    FLEX_END("flex-end"),

    /**
     * 在主轴上居中对齐
     */
    @JSONField(name = "center")
    CENTER("center"),

    /**
     * 首尾贴边，中间元素均匀分布剩余空间
     */
    @JSONField(name = "space-between")
    SPACE_BETWEEN("space-between"),

    /**
     * 每个元素周围分配相同的空间（首尾元素外侧也有空间）
     */
    @JSONField(name = "space-around")
    SPACE_AROUND("space-around"),

    /**
     * 所有间隔（包括首尾与边界）完全相等
     */
    @JSONField(name = "space-evenly")
    SPACE_EVENLY("space-evenly"),

    /**
     * 向书写模式起点对齐（逻辑属性，适配RTL）
     */
    @JSONField(name = "start")
    START("start"),

    /**
     * 向书写模式终点对齐（逻辑属性，适配RTL）
     */
    @JSONField(name = "end")
    END("end"),

    /**
     * 向左对齐（仅适用于水平主轴）
     */
    @JSONField(name = "left")
    LEFT("left"),

    /**
     * 向右对齐（仅适用于水平主轴）
     */
    @JSONField(name = "right")
    RIGHT("right");

    private final String value;

    CSJustifyContent(String value) {
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
