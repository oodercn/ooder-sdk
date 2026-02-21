package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 【大模型读取指南】
 * 本枚举定义CSS font-weight属性的所有可能值
 *
 * 【属性作用】
 * 控制文本字体的粗细程度
 *
 * 【取值对照表】
 * | 枚举值 | CSS值 | 数值 | 说明 |
 * |--------|-------|------|------|
 * | UNSET | "" | - | 未设置，继承父级 |
 * | NORMAL | "normal" | 400 | 正常粗细 |
 * | BOLD | "bold" | 700 | 粗体 |
 * | LIGHTER | "lighter" | - | 比父级更细 |
 * | BOLDER | "bolder" | - | 比父级更粗 |
 * | W100 | "100" | 100 | 极细 |
 * | W200 | "200" | 200 | 特细 |
 * | W300 | "300" | 300 | 细体 |
 * | W400 | "400" | 400 | 正常(同NORMAL) |
 * | W500 | "500" | 500 | 中等 |
 * | W600 | "600" | 600 | 半粗 |
 * | W700 | "700" | 700 | 粗体(同BOLD) |
 * | W800 | "800" | 800 | 特粗 |
 * | W900 | "900" | 900 | 极粗 |
 *
 * 【使用建议】
 * - 正文使用 NORMAL (400)
 * - 标题使用 BOLD (700) 或 W600-W700
 * - 细线文字使用 W300 或 LIGHTER
 * - 极粗强调使用 W900 或 BOLDER
 *
 * 【常见组合】
 * - 标准正文: CSFontWeight.NORMAL
 * - 强调文字: CSFontWeight.BOLD
 * - 轻量标题: CSFontWeight.W300
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSFontWeight {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "normal")
    NORMAL("normal"),

    @JSONField(name = "bold")
    BOLD("bold"),

    @JSONField(name = "lighter")
    LIGHTER("lighter"),

    @JSONField(name = "bolder")
    BOLDER("bolder"),

    @JSONField(name = "100")
    W100("100"),

    @JSONField(name = "200")
    W200("200"),

    @JSONField(name = "300")
    W300("300"),

    @JSONField(name = "400")
    W400("400"),

    @JSONField(name = "500")
    W500("500"),

    @JSONField(name = "600")
    W600("600"),

    @JSONField(name = "700")
    W700("700"),

    @JSONField(name = "800")
    W800("800"),

    @JSONField(name = "900")
    W900("900");

    private final String value;

    CSFontWeight(String value) {
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
