package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS box-sizing属性枚举
 *
 * <p><b>属性作用：</b>定义元素的盒模型计算方式，决定元素的宽度和高度是否包含内边距(padding)和边框(border)。</p>
 *
 * <p><b>取值对照表：</b></p>
 * <table border="1">
 *     <tr>
 *         <th>枚举值</th>
 *         <th>CSS值</th>
 *         <th>说明</th>
 *     </tr>
 *     <tr>
 *         <td>UNSET</td>
 *         <td>""</td>
 *         <td>未设置，继承父元素或浏览器默认行为</td>
 *     </tr>
 *     <tr>
 *         <td>CONTENT_BOX</td>
 *         <td>content-box</td>
 *         <td>标准盒模型，width/height只包含内容区域</td>
 *     </tr>
 *     <tr>
 *         <td>BORDER_BOX</td>
 *         <td>border-box</td>
 *         <td>怪异盒模型，width/height包含内容+padding+border</td>
 *     </tr>
 * </table>
 *
 * <p><b>两种盒模型的区别示意图：</b></p>
 * <pre>
 * 【content-box 标准盒模型】
 * ┌─────────────────────────────┐  ← width/height 只包含 content
 * │         margin              │
 * │   ┌─────────────────────┐   │
 * │   │      border         │   │
 * │   │   ┌─────────────┐   │   │
 * │   │   │   padding   │   │   │
 * │   │   │   ┌─────┐   │   │   │
 * │   │   │   │content│   │   │   │
 * │   │   │   └─────┘   │   │   │
 * │   │   └─────────────┘   │   │
 * │   └─────────────────────┘   │
 * └─────────────────────────────┘
 * 实际占用宽度 = width + padding + border + margin
 *
 * 【border-box 怪异盒模型】
 * ┌─────────────────────────────┐  ← width/height 包含 content+padding+border
 * │         margin              │
 * │   ┌─────────────────────┐   │
 * │   │      border         │   │
 * │   │   ┌─────────────┐   │   │
 * │   │   │   padding   │   │   │
 * │   │   │   ┌─────┐   │   │   │
 * │   │   │   │content│   │   │   │
 * │   │   │   └─────┘   │   │   │
 * │   │   └─────────────┘   │   │
 * │   └─────────────────────┘   │
 * └─────────────────────────────┘
 * 实际占用宽度 = width + margin
 * </pre>
 *
 * <p><b>使用建议：</b></p>
 * <ul>
 *     <li><b>推荐使用 BORDER_BOX：</b>在响应式布局和组件化开发中，BORDER_BOX 更直观，设置 width 就是最终可见宽度，便于计算和布局</li>
 *     <li><b>全局重置：</b>建议在项目全局样式中统一设置 * { box-sizing: border-box; }</li>
 *     <li><b>第三方组件：</b>引入第三方UI组件库时，注意其盒模型设置，避免布局错乱</li>
 *     <li><b>兼容性：</b>IE8+ 支持 box-sizing 属性，现代浏览器完全兼容</li>
 * </ul>
 *
 * <p><b>常见组合：</b></p>
 * <ul>
 *     <li>表单输入框：BORDER_BOX + 固定width + padding，确保输入框尺寸一致</li>
 *     <li>网格布局：BORDER_BOX + 百分比width，避免边框导致换行</li>
 *     <li>卡片组件：BORDER_BOX + 固定width/height，内边距不影响整体尺寸</li>
 *     <li>响应式容器：BORDER_BOX + max-width，内容自适应且边界清晰</li>
 * </ul>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/box-sizing">MDN box-sizing</a>
 */
public enum CSBoxSizing {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "content-box")
    CONTENT_BOX("content-box"),

    @JSONField(name = "border-box")
    BORDER_BOX("border-box");

    private final String value;

    CSBoxSizing(String value) {
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
