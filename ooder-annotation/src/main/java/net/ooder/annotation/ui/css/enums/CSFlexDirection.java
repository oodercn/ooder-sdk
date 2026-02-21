package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS flex-direction属性枚举
 *
 * 【大模型读取指南】
 * 本枚举定义CSS Flexbox布局中flex-direction属性的所有有效取值。
 * 该属性控制Flex容器的主轴方向，决定子元素的排列方向。
 * 在解析UI配置或生成CSS样式时，可通过此枚举获取标准取值。
 *
 * 【属性作用】
 * flex-direction定义Flex容器中主轴的方向，即子元素的排列方向。
 * - 主轴：子元素沿着该轴排列
 * - 交叉轴：垂直于主轴的轴
 * 该属性直接影响justify-content（主轴对齐）和align-items（交叉轴对齐）的行为。
 *
 * 【取值对照表】
 * | 枚举值           | CSS值           | 方向描述                     |
 * |------------------|-----------------|------------------------------|
 * | UNSET            | (空字符串)       | 未设置，使用浏览器默认值      |
 * | ROW              | row             | 水平排列，从左到右            |
 * | ROW_REVERSE      | row-reverse     | 水平排列，从右到左            |
 * | COLUMN           | column          | 垂直排列，从上到下            |
 * | COLUMN_REVERSE   | column-reverse  | 垂直排列，从下到上            |
 *
 * 【方向示意图】
 *
 * ROW (row):
 *     +---+---+---+---+
 *     | 1 | 2 | 3 | 4 |  → 主轴方向（从左到右）
 *     +---+---+---+---+
 *
 * ROW_REVERSE (row-reverse):
 *     +---+---+---+---+
 *     | 4 | 3 | 2 | 1 |  ← 主轴方向（从右到左）
 *     +---+---+---+---+
 *
 * COLUMN (column):
 *     +---+
 *     | 1 |
 *     +---+
 *     | 2 |
 *     +---+
 *     | 3 |
 *     +---+
 *     | 4 |
 *     +---+
 *     ↓
 *     主轴方向（从上到下）
 *
 * COLUMN_REVERSE (column-reverse):
 *     +---+
 *     | 4 |
 *     +---+
 *     | 3 |
 *     +---+
 *     | 2 |
 *     +---+
 *     | 1 |
 *     +---+
 *     ↑
 *     主轴方向（从下到上）
 *
 * 【使用建议】
 * 1. 水平布局（如导航栏、按钮组）：使用ROW
 * 2. 垂直布局（如侧边栏、表单）：使用COLUMN
 * 3. 反向排列（如时间线倒序）：使用ROW_REVERSE或COLUMN_REVERSE
 * 4. 保持默认行为：使用UNSET
 *
 * 【常见组合】
 * - 水平居中布局：ROW + justify-content: center
 * - 垂直居中布局：COLUMN + align-items: center
 * - 水平两端对齐：ROW + justify-content: space-between
 * - 垂直等分布局：COLUMN + justify-content: space-around
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSFlexDirection {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "row")
    ROW("row"),

    @JSONField(name = "row-reverse")
    ROW_REVERSE("row-reverse"),

    @JSONField(name = "column")
    COLUMN("column"),

    @JSONField(name = "column-reverse")
    COLUMN_REVERSE("column-reverse");

    private final String value;

    CSFlexDirection(String value) {
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
