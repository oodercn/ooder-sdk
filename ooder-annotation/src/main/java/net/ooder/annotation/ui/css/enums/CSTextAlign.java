package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS text-align 属性枚举
 * 
 * <p><strong>属性作用：</strong>
 * <p>控制元素内文本内容的水平对齐方式。该属性作用于块级元素，定义其内部行内内容（文本、行内元素）相对于元素边界的对齐方式。
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
 *     <td>未设置，继承或默认行为</td>
 *   </tr>
 *   <tr>
 *     <td>LEFT</td>
 *     <td>left</td>
 *     <td>文本左对齐</td>
 *   </tr>
 *   <tr>
 *     <td>RIGHT</td>
 *     <td>right</td>
 *     <td>文本右对齐</td>
 *   </tr>
 *   <tr>
 *     <td>CENTER</td>
 *     <td>center</td>
 *     <td>文本居中对齐</td>
 *   </tr>
 *   <tr>
 *     <td>JUSTIFY</td>
 *     <td>justify</td>
 *     <td>文本两端对齐，自动调整词间距</td>
 *   </tr>
 *   <tr>
 *     <td>START</td>
 *     <td>start</td>
 *     <td>根据文档书写方向的起始边对齐（LTR为左，RTL为右）</td>
 *   </tr>
 *   <tr>
 *     <td>END</td>
 *     <td>end</td>
 *     <td>根据文档书写方向的结束边对齐（LTR为右，RTL为左）</td>
 *   </tr>
 * </table>
 * 
 * <p><strong>使用建议：</strong>
 * <ul>
 *   <li>常规文本段落推荐使用 LEFT（中文）或根据语言习惯选择</li>
 *   <li>标题、按钮文字常用 CENTER 实现居中效果</li>
 *   <li>数字、金额显示推荐使用 RIGHT 实现小数点对齐</li>
 *   <li>正式排版、报纸风格内容可使用 JUSTIFY 实现两端对齐</li>
 *   <li>国际化应用优先使用 START/END 替代 LEFT/RIGHT，自动适配 RTL 语言</li>
 * </ul>
 * 
 * <p><strong>常见组合：</strong>
 * <ul>
 *   <li>表格数字列：text-align: right + font-family: monospace</li>
 *   <li>按钮文字：text-align: center + line-height 等于按钮高度实现垂直居中</li>
 *   <li>卡片标题：text-align: center + font-weight: bold</li>
 *   <li>表单标签：text-align: right + padding-right 实现右对齐标签布局</li>
 *   <li>文章正文：text-align: justify + text-indent 实现首行缩进的两端对齐排版</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/text-align">MDN text-align 文档</a>
 */
public enum CSTextAlign {

    /**
     * 未设置，使用继承值或浏览器默认值
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 左对齐，文本靠左边缘排列
     */
    @JSONField(name = "left")
    LEFT("left"),

    /**
     * 右对齐，文本靠右边缘排列
     */
    @JSONField(name = "right")
    RIGHT("right"),

    /**
     * 居中对齐，文本在元素内水平居中
     */
    @JSONField(name = "center")
    CENTER("center"),

    /**
     * 两端对齐，文本左右边缘都对齐，自动调整词间距
     */
    @JSONField(name = "justify")
    JUSTIFY("justify"),

    /**
     * 起始边对齐，根据文档书写方向自动选择左或右对齐
     * LTR（从左到右）语言中表现为左对齐
     * RTL（从右到左）语言中表现为右对齐
     */
    @JSONField(name = "start")
    START("start"),

    /**
     * 结束边对齐，根据文档书写方向自动选择右或左对齐
     * LTR（从左到右）语言中表现为右对齐
     * RTL（从右到左）语言中表现为左对齐
     */
    @JSONField(name = "end")
    END("end");

    private final String value;

    CSTextAlign(String value) {
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
