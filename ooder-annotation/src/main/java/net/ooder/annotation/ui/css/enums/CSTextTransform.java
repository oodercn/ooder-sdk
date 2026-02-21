package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS text-transform 属性枚举
 * <p>
 * 作用：控制元素的文本大小写转换方式，仅影响视觉呈现，不改变实际文本内容。
 * 适用于标题、按钮、导航等需要统一文本风格的UI组件。
 * </p>
 *
 * <h3>取值对照表</h3>
 * <table border="1">
 *     <tr><th>枚举值</th><th>CSS值</th><th>效果说明</th></tr>
 *     <tr><td>UNSET</td><td>""</td><td>未设置，继承父元素或浏览器默认值</td></tr>
 *     <tr><td>NONE</td><td>"none"</td><td>无转换，保持原文本</td></tr>
 *     <tr><td>CAPITALIZE</td><td>"capitalize"</td><td>首字母大写，每个单词的第一个字母转为大写</td></tr>
 *     <tr><td>UPPERCASE</td><td>"uppercase"</td><td>全部大写，所有字母转为大写</td></tr>
 *     <tr><td>LOWERCASE</td><td>"lowercase"</td><td>全部小写，所有字母转为小写</td></tr>
 * </table>
 *
 * <h3>使用建议</h3>
 * <ul>
 *     <li>UPPERCASE：适合按钮文字、标签、警告提示，增强视觉冲击力</li>
 *     <li>CAPITALIZE：适合标题、人名、菜单项，保持可读性且显正式</li>
 *     <li>LOWERCASE：适合装饰性文本、艺术字，营造轻松氛围</li>
 *     <li>NONE：适合正文内容，保持用户输入原样</li>
 *     <li>UNSET：适合需要继承上级样式的场景</li>
 * </ul>
 *
 * <h3>常见组合</h3>
 * <ul>
 *     <li>按钮样式：UPPERCASE + 字间距(letter-spacing: 1px) + 粗体</li>
 *     <li>卡片标题：CAPITALIZE + 中等字号</li>
 *     <li>导航菜单：UPPERCASE + 小号字体 + 灰色</li>
 *     <li>表单标签：NONE 或 CAPITALIZE，保持简洁</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/text-transform">MDN text-transform</a>
 */
public enum CSTextTransform {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "none")
    NONE("none"),

    @JSONField(name = "capitalize")
    CAPITALIZE("capitalize"),

    @JSONField(name = "uppercase")
    UPPERCASE("uppercase"),

    @JSONField(name = "lowercase")
    LOWERCASE("lowercase");

    private final String value;

    CSTextTransform(String value) {
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
