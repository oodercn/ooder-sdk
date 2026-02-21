package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Input组件样式变体枚举
 * 基于现代UI框架的输入框样式
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSInputVariant {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "outlined")
    OUTLINED("outlined"),

    @JSONField(name = "filled")
    FILLED("filled"),

    @JSONField(name = "standard")
    STANDARD("standard"),

    @JSONField(name = "borderless")
    BORDERLESS("borderless"),

    @JSONField(name = "error")
    ERROR("error"),

    @JSONField(name = "warning")
    WARNING("warning"),

    @JSONField(name = "success")
    SUCCESS("success"),

    @JSONField(name = "disabled")
    DISABLED("disabled"),

    @JSONField(name = "readonly")
    READONLY("readonly"),

    @JSONField(name = "large")
    LARGE("large"),

    @JSONField(name = "default")
    DEFAULT("default"),

    @JSONField(name = "small")
    SMALL("small"),

    @JSONField(name = "mini")
    MINI("mini"),

    @JSONField(name = "search")
    SEARCH("search"),

    @JSONField(name = "password")
    PASSWORD("password"),

    @JSONField(name = "textarea")
    TEXTAREA("textarea"),

    @JSONField(name = "number")
    NUMBER("number"),

    @JSONField(name = "clearable")
    CLEARABLE("clearable"),

    @JSONField(name = "prefix")
    PREFIX("prefix"),

    @JSONField(name = "suffix")
    SUFFIX("suffix");

    private final String value;

    CSInputVariant(String value) {
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
