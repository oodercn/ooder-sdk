package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS border-style属性枚举
 * 
 * 【大模型读取指南】
 * 本枚举定义CSS border-style属性的所有有效值，用于控制元素边框的样式外观。
 * 每个枚举值通过JSONField注解映射到对应的CSS字符串值。
 * 使用场景：UI组件库中设置边框样式，如按钮、输入框、卡片、分割线等组件的边框渲染。
 * 
 * 【属性作用】
 * border-style属性设置元素边框的线条样式，是边框三要素（width/style/color）中的核心属性：
 * - 决定边框是否可见以及以何种形式呈现
 * - 必须与border-width配合使用才能显示边框
 * - 可单独设置四边（top/right/bottom/left）或使用简写属性统一设置
 * 
 * 【取值对照表】
 * | 枚举值  | CSS值   | 视觉样式说明                          | 适用场景                  |
 * |--------|---------|--------------------------------------|--------------------------|
 * | UNSET  | ""      | 未设置，继承或恢复默认值                | 初始状态，使用父级或默认样式 |
 * | NONE   | none    | 无边框，边框宽度为0                    | 隐藏边框，扁平化设计        |
 * | HIDDEN | hidden  | 隐藏边框，与none类似但优先级更高        | 表格边框冲突解决            |
 * | SOLID  | solid   | 实线 ————————————                     | 常规边框，最常用            |
 * | DASHED | dashed  | 虚线 - - - - - - - - -                | 分隔线、次要边框            |
 * | DOTTED | dotted  | 点线 ···············                  | 装饰性边框、提示框          |
 * | DOUBLE | double  | 双线 ═══════════════                  | 强调边框、表格标题          |
 * | GROOVE | groove  | 3D凹槽效果（内阴影）                    | 立体按钮、复古风格          |
 * | RIDGE  | ridge   | 3D凸槽效果（外凸起）                    | 立体面板、浮雕效果          |
 * | INSET  | inset   | 3D内嵌效果（元素凹陷）                  | 按下状态、内嵌输入框        |
 * | OUTSET | outset  | 3D外凸效果（元素凸起）                  | 凸起按钮、可点击状态        |
 * 
 * 【样式分类】
 * 1. 无边框类：
 *    UNSET（未设置）, NONE（无）, HIDDEN（隐藏）
 * 
 * 2. 基础线条类：
 *    SOLID（实线）- 最常用，清晰明确
 *    DASHED（虚线）- 用于分隔、裁剪提示
 *    DOTTED（点线）- 用于装饰、连接
 * 
 * 3. 特殊线条类：
 *    DOUBLE（双线）- 强调、表格
 * 
 * 4. 3D立体效果类（依赖border-color和背景色）：
 *    GROOVE（凹槽）, RIDGE（凸槽）, INSET（内嵌）, OUTSET（外凸）
 *    注意：3D效果需要配合适当的border-width（通常3px以上）才能明显呈现
 * 
 * 【使用建议】
 * - 常规UI边框优先使用 SOLID，简洁清晰
 * - 表单输入框使用 SOLID，聚焦时可切换为 INSET 增强立体感
 * - 分割线使用 SOLID 或 DASHED，根据视觉层次选择
 * - 3D效果（GROOVE/RIDGE/INSET/OUTSET）适合复古风格或需要立体感的场景
 * - HIDDEN 主要用于解决表格边框合并时的冲突问题
 * - 无边框设计趋势下，NONE 常用于扁平化按钮和卡片
 * 
 * 【常见组合】
 * - 标准输入框：SOLID + border-width: 1px + border-color: #ccc
 * - 主按钮边框：SOLID + border-width: 1px + 与背景色相近的边框色
 * - 虚线分隔线：DASHED + border-width: 1px + border-color: #ddd
 * - 3D凸起按钮：OUTSET + border-width: 3px + 浅色边框
 * - 3D凹陷面板：INSET + border-width: 2px + 灰色边框
 * - 表格边框：SOLID 或 HIDDEN（配合border-collapse使用）
 * - 卡片阴影边框：NONE + 使用box-shadow实现阴影效果
 * - 禁用状态：NONE 或 SOLID + 浅灰色
 * 
 * 【注意事项】
 * - border-style为NONE或HIDDEN时，border-width失效（边框不占据空间）
 * - 3D效果的颜色由浏览器根据border-color自动计算高光和阴影
 * - 部分3D效果在现代扁平化设计中已较少使用
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSBorderStyle {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "none")
    NONE("none"),

    @JSONField(name = "hidden")
    HIDDEN("hidden"),

    @JSONField(name = "dotted")
    DOTTED("dotted"),

    @JSONField(name = "dashed")
    DASHED("dashed"),

    @JSONField(name = "solid")
    SOLID("solid"),

    @JSONField(name = "double")
    DOUBLE("double"),

    @JSONField(name = "groove")
    GROOVE("groove"),

    @JSONField(name = "ridge")
    RIDGE("ridge"),

    @JSONField(name = "inset")
    INSET("inset"),

    @JSONField(name = "outset")
    OUTSET("outset");

    private final String value;

    CSBorderStyle(String value) {
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
