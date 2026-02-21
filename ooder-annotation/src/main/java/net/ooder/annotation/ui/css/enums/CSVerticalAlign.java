package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS vertical-align 属性枚举
 * 
 * <p><strong>属性作用：</strong>
 * <p>设置行内元素（inline）或表格单元格（table-cell）的垂直对齐方式。
 * 该属性定义了元素的顶部、中部或底部相对于父元素基线或行的对齐位置。
 * 注意：vertical-align 仅对行内元素和表格单元格有效，对块级元素无效。
 * 
 * <p><strong>取值对照表：</strong>
 * <table border="1">
 *   <tr>
 *     <th>枚举值</th>
 *     <th>CSS 值</th>
 *     <th>说明</th>
 *   </tr>
 *   <tr>
 *     <td>UNSET</td>
 *     <td>""</td>
 *     <td>未设置，继承或默认行为（行内元素默认值为 baseline）</td>
 *   </tr>
 *   <tr>
 *     <td>BASELINE</td>
 *     <td>baseline</td>
 *     <td>默认值，元素基线与父元素基线对齐</td>
 *   </tr>
 *   <tr>
 *     <td>TOP</td>
 *     <td>top</td>
 *     <td>元素顶部与行内最高元素的顶部对齐</td>
 *   </tr>
 *   <tr>
 *     <td>MIDDLE</td>
 *     <td>middle</td>
 *     <td>元素中部与父元素基线加上小写x高度的一半对齐</td>
 *   </tr>
 *   <tr>
 *     <td>BOTTOM</td>
 *     <td>bottom</td>
 *     <td>元素底部与行内最低元素的底部对齐</td>
 *   </tr>
 *   <tr>
 *     <td>TEXT_TOP</td>
 *     <td>text-top</td>
 *     <td>元素顶部与父元素字体顶部对齐</td>
 *   </tr>
 *   <tr>
 *     <td>TEXT_BOTTOM</td>
 *     <td>text-bottom</td>
 *     <td>元素底部与父元素字体底部对齐</td>
 *   </tr>
 *   <tr>
 *     <td>SUB</td>
 *     <td>sub</td>
 *     <td>元素基线降低到下标位置（适合化学式、数学公式）</td>
 *   </tr>
 *   <tr>
 *     <td>SUPER</td>
 *     <td>super</td>
 *     <td>元素基线升高到上标位置（适合脚注标记、指数）</td>
 *   </tr>
 * </table>
 * 
 * <p><strong>使用建议：</strong>
 * <ul>
 *   <li>图文混排时，使用 MIDDLE 使图片与文字垂直居中对齐</li>
 *   <li>图标与文字并排时，使用 TEXT_TOP 或 TEXT_BOTTOM 实现精确对齐</li>
 *   <li>表格单元格内垂直居中，推荐使用 MIDDLE</li>
 *   <li>化学式（如 H₂O）使用 SUB 实现下标效果</li>
 *   <li>数学指数（如 x²）使用 SUPER 实现上标效果</li>
 *   <li>需要与行最高/最低元素对齐时，使用 TOP 或 BOTTOM</li>
 * </ul>
 * 
 * <p><strong>常见组合：</strong>
 * <ul>
 *   <li>图标+文字：vertical-align: middle + display: inline-block</li>
 *   <li>表格单元格垂直居中：vertical-align: middle + text-align: center</li>
 *   <li>输入框与按钮对齐：vertical-align: middle 统一设置</li>
 *   <li>图片与文字基线对齐：vertical-align: baseline（默认）或 text-bottom 消除底部间隙</li>
 *   <li>化学公式：vertical-align: sub + font-size: smaller</li>
 *   <li>上标标注：vertical-align: super + font-size: smaller + color 区分</li>
 *   <li>多行文本垂直居中：父元素 display: table-cell + vertical-align: middle</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/vertical-align">MDN vertical-align 文档</a>
 */
public enum CSVerticalAlign {

    /**
     * 未设置，使用继承值或浏览器默认值（行内元素默认为 baseline）
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 基线对齐，元素基线与父元素基线对齐，这是行内元素的默认值
     */
    @JSONField(name = "baseline")
    BASELINE("baseline"),

    /**
     * 下标对齐，元素基线降低到下标位置
     * 适用于化学式、数学公式等场景
     */
    @JSONField(name = "sub")
    SUB("sub"),

    /**
     * 上标对齐，元素基线升高到上标位置
     * 适用于脚注标记、数学指数等场景
     */
    @JSONField(name = "super")
    SUPER("super"),

    /**
     * 顶部对齐，元素顶部与行内最高元素的顶部对齐
     */
    @JSONField(name = "top")
    TOP("top"),

    /**
     * 文本顶部对齐，元素顶部与父元素字体顶部对齐
     * 适用于图标与文字精确对齐
     */
    @JSONField(name = "text-top")
    TEXT_TOP("text-top"),

    /**
     * 居中对齐，元素中部与父元素基线加上小写x高度的一半对齐
     * 最常用的垂直居中方式，适用于图文混排
     */
    @JSONField(name = "middle")
    MIDDLE("middle"),

    /**
     * 底部对齐，元素底部与行内最低元素的底部对齐
     */
    @JSONField(name = "bottom")
    BOTTOM("bottom"),

    /**
     * 文本底部对齐，元素底部与父元素字体底部对齐
     * 可消除图片等行内块元素底部的默认间隙
     */
    @JSONField(name = "text-bottom")
    TEXT_BOTTOM("text-bottom");

    private final String value;

    CSVerticalAlign(String value) {
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
