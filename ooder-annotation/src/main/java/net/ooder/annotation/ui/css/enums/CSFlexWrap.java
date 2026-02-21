package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS flex-wrap属性枚举
 * 
 * <p><strong>属性作用</strong></p>
 * <p>flex-wrap属性用于设置flex容器内子元素（flex item）的换行方式。
 * 当flex容器的主轴空间不足以容纳所有子元素时，该属性控制子元素是否换行以及如何换行。</p>
 * 
 * <p><strong>取值对照表</strong></p>
 * <table border="1">
 *   <tr>
 *     <th>枚举值</th>
 *     <th>CSS值</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>UNSET</td>
 *     <td>""</td>
 *     <td>未设置，使用默认值（继承或初始值）</td>
 *   </tr>
 *   <tr>
 *     <td>NOWRAP</td>
 *     <td>nowrap</td>
 *     <td>不换行，所有子元素在一行显示（默认值）</td>
 *   </tr>
 *   <tr>
 *     <td>WRAP</td>
 *     <td>wrap</td>
 *     <td>换行，子元素按顺序从上到下排列</td>
 *   </tr>
 *   <tr>
 *     <td>WRAP_REVERSE</td>
 *     <td>wrap-reverse</td>
 *     <td>反向换行，子元素从下到上排列</td>
 *   </tr>
 * </table>
 * 
 * <p><strong>使用建议</strong></p>
 * <ul>
 *   <li>NOWRAP：适用于子元素数量较少且容器宽度充足的场景，保持单行布局</li>
 *   <li>WRAP：适用于响应式布局，当屏幕宽度不足时自动换行，是最常用的换行方式</li>
 *   <li>WRAP_REVERSE：适用于特殊布局需求，如底部固定的标签栏等场景</li>
 *   <li>UNSET：需要继承父级设置或重置为默认时使用</li>
 * </ul>
 * 
 * <p><strong>常见组合</strong></p>
 * <ul>
 *   <li>flex-direction: row + flex-wrap: wrap：水平排列，空间不足时换行（常用响应式布局）</li>
 *   <li>flex-direction: column + flex-wrap: wrap：垂直排列，高度不足时换列</li>
 *   <li>flex-wrap: wrap + justify-content: center：换行后居中对齐</li>
 *   <li>flex-wrap: wrap + align-content: flex-start：多行时顶部对齐</li>
 * </ul>
 * 
 * <p><strong>注意事项</strong></p>
 * <ul>
 *   <li>该属性仅在flex容器上生效（display: flex 或 display: inline-flex）</li>
 *   <li>换行后的行间距可通过align-content属性控制</li>
 *   <li>与flex-direction属性可简写为flex-flow（如：flex-flow: row wrap）</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-wrap">MDN flex-wrap文档</a>
 */
public enum CSFlexWrap {

    /**
     * 未设置
     * <p>不指定flex-wrap值，使用继承值或浏览器默认值</p>
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 不换行
     * <p>所有flex子元素在一行显示，即使超出容器宽度也不会换行（默认值）</p>
     */
    @JSONField(name = "nowrap")
    NOWRAP("nowrap"),

    /**
     * 换行
     * <p>当flex子元素总宽度超出容器时，自动换到下一行，按正常顺序排列</p>
     */
    @JSONField(name = "wrap")
    WRAP("wrap"),

    /**
     * 反向换行
     * <p>当flex子元素总宽度超出容器时，换行但顺序反转，从下到上排列</p>
     */
    @JSONField(name = "wrap-reverse")
    WRAP_REVERSE("wrap-reverse");

    private final String value;

    CSFlexWrap(String value) {
        this.value = value;
    }

    /**
     * 获取CSS属性值
     * 
     * @return CSS flex-wrap属性对应的字符串值
     */
    public String getValue() {
        return value;
    }

    /**
     * 返回CSS属性值字符串
     * 
     * @return 与getValue()相同，CSS属性值
     */
    @Override
    public String toString() {
        return value;
    }
}
