package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Table/TreeGrid组件样式变体枚举
 * 基于现代UI框架的表格样式
 *
 * <p><b>属性作用：</b>
 * 定义表格组件的视觉样式和交互行为变体，控制表格的边框、行样式、悬停效果、
 * 密度、布局模式、滚动行为、粘性定位以及特殊功能（如树形、编辑、拖拽等）。
 * </p>
 *
 * <p><b>取值对照表：</b>
 * <table border="1">
 *   <tr><th>枚举值</th><th>JSON值</th><th>说明</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>未设置，使用默认样式</td></tr>
 *   <tr><td>DEFAULT</td><td>"default"</td><td>默认样式</td></tr>
 *   <tr><td>BORDERED</td><td>"bordered"</td><td>带边框样式，单元格有边框线</td></tr>
 *   <tr><td>BORDERLESS</td><td>"borderless"</td><td>无边框样式</td></tr>
 *   <tr><td>STRIPED</td><td>"striped"</td><td>斑马纹样式，交替行背景色</td></tr>
 *   <tr><td>HOVER</td><td>"hover"</td><td>悬停效果，鼠标悬停时高亮行</td></tr>
 *   <tr><td>SELECTABLE</td><td>"selectable"</td><td>可选择行</td></tr>
 *   <tr><td>COMPACT</td><td>"compact"</td><td>紧凑模式，减小行高和间距</td></tr>
 *   <tr><td>COMFORTABLE</td><td>"comfortable"</td><td>舒适模式，适中的行高</td></tr>
 *   <tr><td>SPACIOUS</td><td>"spacious"</td><td>宽松模式，增大行高和间距</td></tr>
 *   <tr><td>FIXED</td><td>"fixed"</td><td>固定列宽布局</td></tr>
 *   <tr><td>AUTO</td><td>"auto"</td><td>自动列宽布局</td></tr>
 *   <tr><td>RESPONSIVE</td><td>"responsive"</td><td>响应式布局，适配不同屏幕</td></tr>
 *   <tr><td>SCROLLABLE</td><td>"scrollable"</td><td>可滚动，内容超出时显示滚动条</td></tr>
 *   <tr><td>STICKY_HEADER</td><td>"sticky-header"</td><td>表头粘性定位，滚动时表头固定</td></tr>
 *   <tr><td>STICKY_COLUMN</td><td>"sticky-column"</td><td>首列粘性定位，横向滚动时首列固定</td></tr>
 *   <tr><td>ZEBRA</td><td>"zebra"</td><td>斑马纹（同STRIPED）</td></tr>
 *   <tr><td>LINE</td><td>"line"</td><td>行线样式</td></tr>
 *   <tr><td>CARD</td><td>"card"</td><td>卡片样式</td></tr>
 *   <tr><td>TREE</td><td>"tree"</td><td>树形表格</td></tr>
 *   <tr><td>EDITABLE</td><td>"editable"</td><td>可编辑表格</td></tr>
 *   <tr><td>EXPANDABLE</td><td>"expandable"</td><td>可展开行</td></tr>
 *   <tr><td>DRAGGABLE</td><td>"draggable"</td><td>可拖拽排序</td></tr>
 *   <tr><td>VIRTUAL</td><td>"virtual"</td><td>虚拟滚动，用于大数据量</td></tr>
 * </table>
 * </p>
 *
 * <p><b>使用建议：</b>
 * <ul>
 *   <li>样式类（BORDERED/BORDERLESS/STRIPED/HOVER）可组合使用，如 BORDERED + STRIPED + HOVER</li>
 *   <li>密度类（COMPACT/COMFORTABLE/SPACIOUS）互斥，只能选其一</li>
 *   <li>布局类（FIXED/AUTO）互斥，根据数据特点选择</li>
 *   <li>功能类（TREE/EDITABLE/EXPANDABLE/DRAGGABLE/VIRTUAL）根据业务需求单独配置</li>
 *   <li>大数据量时建议使用 VIRTUAL 虚拟滚动提升性能</li>
 * </ul>
 * </p>
 *
 * <p><b>常见组合：</b>
 * <ul>
 *   <li>基础列表：DEFAULT + BORDERED + HOVER</li>
 *   <li>数据报表：BORDERED + STRIPED + COMPACT</li>
 *   <li>树形数据：TREE + BORDERED + HOVER</li>
 *   <li>大数据表格：VIRTUAL + STICKY_HEADER + COMPACT</li>
 *   <li>可编辑表格：EDITABLE + BORDERED + COMFORTABLE</li>
 *   <li>固定列表格：STICKY_HEADER + STICKY_COLUMN + BORDERED</li>
 * </ul>
 * </p>
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSTableVariant {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "default")
    DEFAULT("default"),

    @JSONField(name = "bordered")
    BORDERED("bordered"),

    @JSONField(name = "borderless")
    BORDERLESS("borderless"),

    @JSONField(name = "striped")
    STRIPED("striped"),

    @JSONField(name = "hover")
    HOVER("hover"),

    @JSONField(name = "selectable")
    SELECTABLE("selectable"),

    @JSONField(name = "compact")
    COMPACT("compact"),

    @JSONField(name = "comfortable")
    COMFORTABLE("comfortable"),

    @JSONField(name = "spacious")
    SPACIOUS("spacious"),

    @JSONField(name = "fixed")
    FIXED("fixed"),

    @JSONField(name = "auto")
    AUTO("auto"),

    @JSONField(name = "responsive")
    RESPONSIVE("responsive"),

    @JSONField(name = "scrollable")
    SCROLLABLE("scrollable"),

    @JSONField(name = "sticky-header")
    STICKY_HEADER("sticky-header"),

    @JSONField(name = "sticky-column")
    STICKY_COLUMN("sticky-column"),

    @JSONField(name = "zebra")
    ZEBRA("zebra"),

    @JSONField(name = "line")
    LINE("line"),

    @JSONField(name = "card")
    CARD("card"),

    @JSONField(name = "tree")
    TREE("tree"),

    @JSONField(name = "editable")
    EDITABLE("editable"),

    @JSONField(name = "expandable")
    EXPANDABLE("expandable"),

    @JSONField(name = "draggable")
    DRAGGABLE("draggable"),

    @JSONField(name = "virtual")
    VIRTUAL("virtual");

    private final String value;

    CSTableVariant(String value) {
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
