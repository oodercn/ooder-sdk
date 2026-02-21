package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS white-space属性枚举
 * <p>
 * 控制元素内空白字符（空格、换行、制表符）的处理方式，以及文本是否自动换行。
 * 该属性影响文本的渲染方式和布局表现。
 * </p>
 *
 * <h3>取值对照表</h3>
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS值</th><th>空白折叠</th><th>换行符</th><th>自动换行</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>-</td><td>-</td><td>-</td></tr>
 *   <tr><td>NORMAL</td><td>normal</td><td>是</td><td>视为空格</td><td>是</td></tr>
 *   <tr><td>NOWRAP</td><td>nowrap</td><td>是</td><td>视为空格</td><td>否</td></tr>
 *   <tr><td>PRE</td><td>pre</td><td>否</td><td>保留</td><td>否</td></tr>
 *   <tr><td>PRE_WRAP</td><td>pre-wrap</td><td>否</td><td>保留</td><td>是</td></tr>
 *   <tr><td>PRE_LINE</td><td>pre-line</td><td>是</td><td>保留</td><td>是</td></tr>
 * </table>
 *
 * <h3>使用建议</h3>
 * <ul>
 *   <li><b>NORMAL</b>: 默认值，适用于大多数文本内容，自动处理空白并换行</li>
 *   <li><b>NOWRAP</b>: 单行文本不换行，适用于按钮文字、表格标题等</li>
 *   <li><b>PRE</b>: 保留原始格式，适用于显示代码块、预格式化文本</li>
 *   <li><b>PRE_WRAP</b>: 保留格式但允许自动换行，适用于长代码块</li>
 *   <li><b>PRE_LINE</b>: 合并空格但保留换行，适用于用户输入的多行文本</li>
 * </ul>
 *
 * <h3>常见组合</h3>
 * <ul>
 *   <li>代码显示: PRE + overflow-x: auto</li>
 *   <li>单行省略: NOWRAP + overflow: hidden + text-overflow: ellipsis</li>
 *   <li>表单输入: PRE_LINE 保留用户输入的换行</li>
 *   <li>表格单元格: NOWRAP 防止表头换行</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/white-space">MDN white-space</a>
 */
public enum CSWhiteSpace {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "normal")
    NORMAL("normal"),

    @JSONField(name = "nowrap")
    NOWRAP("nowrap"),

    @JSONField(name = "pre")
    PRE("pre"),

    @JSONField(name = "pre-wrap")
    PRE_WRAP("pre-wrap"),

    @JSONField(name = "pre-line")
    PRE_LINE("pre-line");

    private final String value;

    CSWhiteSpace(String value) {
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
