package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS visibility属性枚举
 * 
 * <p>作用：控制元素的可见性，元素在页面中仍保留占位空间</p>
 * 
 * <p>与display:none的区别：
 * <ul>
 *   <li>visibility:hidden - 元素不可见，但仍占据布局空间，不影响文档流</li>
 *   <li>display:none - 元素完全移除，不占据空间，影响文档流</li>
 * </ul>
 * </p>
 * 
 * <p>取值对照表：
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS值</th><th>说明</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>未设置，继承或默认行为</td></tr>
 *   <tr><td>VISIBLE</td><td>"visible"</td><td>元素可见（默认值）</td></tr>
 *   <tr><td>HIDDEN</td><td>"hidden"</td><td>元素不可见，但保留占位空间</td></tr>
 *   <tr><td>COLLAPSE</td><td>"collapse"</td><td>用于表格行/列，折叠后不影响表格布局</td></tr>
 * </table>
 * </p>
 * 
 * <p>使用建议：
 * <ul>
 *   <li>需要保留布局占位时选择HIDDEN（如：表单验证错误提示的显示/隐藏）</li>
 *   <li>表格行/列的显示切换优先使用COLLAPSE</li>
 *   <li>需要完全移除元素时使用display:none而非本属性</li>
 * </ul>
 * </p>
 * 
 * <p>常见组合：
 * <ul>
 *   <li>与opacity配合使用实现淡入淡出效果</li>
 *   <li>与transition配合实现平滑的显隐过渡动画</li>
 *   <li>表格组件中配合COLLAPSE实现行/列的折叠展开</li>
 * </ul>
 * </p>
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSVisibility {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "visible")
    VISIBLE("visible"),

    @JSONField(name = "hidden")
    HIDDEN("hidden"),

    @JSONField(name = "collapse")
    COLLAPSE("collapse");

    private final String value;

    CSVisibility(String value) {
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
