package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS font-style属性枚举
 * 
 * <p><b>属性作用：</b>
 * 设置字体样式，控制文本是否以斜体、倾斜或正常样式显示。该属性影响元素内文本的字体呈现风格。
 * 
 * <p><b>取值对照表：</b>
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS值</th><th>说明</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>未设置，使用浏览器默认值或继承值</td></tr>
 *   <tr><td>NORMAL</td><td>"normal"</td><td>正常字体样式，不倾斜</td></tr>
 *   <tr><td>ITALIC</td><td>"italic"</td><td>斜体，使用字体本身的斜体字形</td></tr>
 *   <tr><td>OBLIQUE</td><td>"oblique"</td><td>倾斜，将正常字体倾斜显示</td></tr>
 * </table>
 * 
 * <p><b>使用建议：</b>
 * <ul>
 *   <li>ITALIC vs OBLIQUE：ITALIC使用字体设计师专门设计的斜体字形，OBLIQUE只是机械倾斜。优先使用ITALIC获得更好的视觉效果。</li>
 *   <li>中文内容：中文字体通常没有专门的斜体字形，使用ITALIC时浏览器会自动倾斜显示。</li>
 *   <li>可读性：长文本段落不建议使用斜体，会降低阅读舒适度，适合用于强调、引用或标题。</li>
 *   <li>兼容性：所有现代浏览器均支持此属性。</li>
 * </ul>
 * 
 * <p><b>常见组合：</b>
 * <ul>
 *   <li>标题强调：font-style: ITALIC + font-weight: BOLD</li>
 *   <li>引用块：font-style: ITALIC + border-left + padding-left</li>
 *   <li>标签/徽章：font-style: NORMAL + font-weight: 600 + text-transform: UPPERCASE</li>
 *   <li>注释/说明文字：font-style: ITALIC + color: gray + font-size: small</li>
 *   <li>按钮文字：font-style: NORMAL + font-weight: 500</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see CSFontWeight
 * @see CSTextDecoration
 */
public enum CSFontStyle {

    /**
     * 未设置，使用浏览器默认值或继承值
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 正常字体样式，不倾斜
     */
    @JSONField(name = "normal")
    NORMAL("normal"),

    /**
     * 斜体，使用字体本身的斜体字形
     */
    @JSONField(name = "italic")
    ITALIC("italic"),

    /**
     * 倾斜，将正常字体倾斜显示
     */
    @JSONField(name = "oblique")
    OBLIQUE("oblique");

    private final String value;

    CSFontStyle(String value) {
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
