package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS display属性枚举
 * 
 * 【大模型读取指南】
 * 本枚举定义CSS display属性的所有有效值，用于控制元素的显示类型和布局方式。
 * 每个枚举值通过JSONField注解映射到对应的CSS字符串值。
 * 使用场景：UI组件库中设置元素的显示模式，如布局容器、隐藏/显示控制等。
 * 
 * 【属性作用】
 * display属性控制元素的显示类型和布局方式，决定元素在文档流中的行为：
 * - 是否生成盒模型
 * - 是块级还是行内显示
 * - 使用何种布局算法（流式、Flex、Grid等）
 * 
 * 【取值对照表】
 * | 枚举值         | CSS值        | 说明                     |
 * |---------------|-------------|-------------------------|
 * | UNSET         | ""          | 未设置，使用默认值          |
 * | NONE          | none        | 隐藏元素，不占据空间         |
 * | BLOCK         | block       | 块级元素，独占一行           |
 * | INLINE        | inline      | 行内元素，不独占一行         |
 * | INLINE_BLOCK  | inline-block| 行内块级，可设宽高          |
 * | FLEX          | flex        | 块级弹性容器                |
 * | INLINE_FLEX   | inline-flex | 行内弹性容器                |
 * | GRID          | grid        | 块级网格容器                |
 * | INLINE_GRID   | inline-grid | 行内网格容器                |
 * | TABLE         | table       | 块级表格                   |
 * | TABLE_CELL    | table-cell  | 表格单元格                 |
 * | TABLE_ROW     | table-row   | 表格行                     |
 * | LIST_ITEM     | list-item   | 列表项                     |
 * | CONTENTS      | contents    | 内容盒，元素本身不产生盒     |
 * 
 * 【布局类型分类】
 * 1. 块级布局（独占一行，可设宽高）：
 *    BLOCK, FLEX, GRID, TABLE
 * 
 * 2. 行内布局（不独占一行）：
 *    INLINE
 * 
 * 3. 行内块级（不独占一行，但可设宽高）：
 *    INLINE_BLOCK, INLINE_FLEX, INLINE_GRID
 * 
 * 4. Flex弹性布局：
 *    FLEX（块级容器）, INLINE_FLEX（行内容器）
 * 
 * 5. Grid网格布局：
 *    GRID（块级容器）, INLINE_GRID（行内容器）
 * 
 * 6. 表格布局：
 *    TABLE, TABLE_CELL, TABLE_ROW
 * 
 * 7. 其他：
 *    NONE（隐藏）, LIST_ITEM（列表）, CONTENTS（内容盒）, UNSET（未设置）
 * 
 * 【使用建议】
 * - 默认布局使用 BLOCK 或 INLINE
 * - 现代响应式布局优先使用 FLEX 或 GRID
 * - 需要隐藏元素使用 NONE（注意：元素会从文档流中移除）
 * - 表单元素常用 INLINE_BLOCK 实现行内对齐
 * - 表格数据展示使用 TABLE 系列
 * 
 * 【常见组合】
 * - 水平居中布局：父元素 FLEX + justify-content: center
 * - 垂直居中布局：父元素 FLEX + align-items: center
 * - 网格卡片布局：父元素 GRID + grid-template-columns
 * - 行内按钮组：父元素 INLINE_FLEX 或子元素 INLINE_BLOCK
 * - 标签页切换：激活状态 BLOCK，非激活状态 NONE
 * - 导航菜单：父元素 FLEX，子元素 INLINE_BLOCK 或 BLOCK
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSDisplay {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "none")
    NONE("none"),
    
    @JSONField(name = "block")
    BLOCK("block"),
    
    @JSONField(name = "inline")
    INLINE("inline"),
    
    @JSONField(name = "inline-block")
    INLINE_BLOCK("inline-block"),
    
    @JSONField(name = "flex")
    FLEX("flex"),
    
    @JSONField(name = "inline-flex")
    INLINE_FLEX("inline-flex"),
    
    @JSONField(name = "grid")
    GRID("grid"),
    
    @JSONField(name = "inline-grid")
    INLINE_GRID("inline-grid"),
    
    @JSONField(name = "table")
    TABLE("table"),
    
    @JSONField(name = "table-cell")
    TABLE_CELL("table-cell"),
    
    @JSONField(name = "table-row")
    TABLE_ROW("table-row"),
    
    @JSONField(name = "list-item")
    LIST_ITEM("list-item"),
    
    @JSONField(name = "contents")
    CONTENTS("contents");
    
    private final String value;
    
    CSDisplay(String value) {
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
