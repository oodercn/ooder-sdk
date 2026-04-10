package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS text-decoration属性枚举
 * <p>
 * 属性作用：控制文本的装饰线样式，用于为文本添加下划线、上划线、删除线等视觉效果。
 * <p>
 * 取值对照表：
 * <table border="1">
 *     <tr><th>枚举值</th><th>CSS值</th><th>说明</th></tr>
 *     <tr><td>UNSET</td><td>""</td><td>未设置，继承父元素或浏览器默认样式</td></tr>
 *     <tr><td>NONE</td><td>"none"</td><td>无装饰线</td></tr>
 *     <tr><td>UNDERLINE</td><td>"underline"</td><td>下划线，位于文本基线下方</td></tr>
 *     <tr><td>OVERLINE</td><td>"overline"</td><td>上划线，位于文本基线上方</td></tr>
 *     <tr><td>LINE_THROUGH</td><td>"line-through"</td><td>删除线，贯穿文本中部</td></tr>
 * </table>
 * <p>
 * 使用建议：
 * <ul>
 *     <li>链接默认样式：超链接通常使用 UNDERLINE 表示可点击</li>
 *     <li>删除内容：已失效或过期的文本使用 LINE_THROUGH 标记</li>
 *     <li>标题装饰：OVERLINE 可用于特殊标题效果，但使用较少</li>
 *     <li>清除装饰：NONE 用于去除链接默认下划线或其他继承的装饰线</li>
 * </ul>
 * <p>
 * 常见组合：
 * <ul>
 *     <li>链接无下划线：text-decoration: NONE; 配合 color 定义链接颜色</li>
 *     <li>删除线效果：LINE_THROUGH 常用于原价、已删除内容</li>
 *     <li>下划线强调：UNDERLINE 用于需要突出显示的文本</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSTextDecoration {

    /**
     * 未设置，继承父元素或浏览器默认样式
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 无装饰线，用于清除继承的装饰效果
     */
    @JSONField(name = "none")
    NONE("none"),

    /**
     * 下划线，位于文本基线下方
     */
    @JSONField(name = "underline")
    UNDERLINE("underline"),

    /**
     * 上划线，位于文本基线上方
     */
    @JSONField(name = "overline")
    OVERLINE("overline"),

    /**
     * 删除线，贯穿文本中部
     */
    @JSONField(name = "line-through")
    LINE_THROUGH("line-through");

    private final String value;

    CSTextDecoration(String value) {
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
