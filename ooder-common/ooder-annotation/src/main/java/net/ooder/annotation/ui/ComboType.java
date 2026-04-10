package net.ooder.annotation.ui;

import net.ooder.annotation.IconEnumstype;

public enum ComboType implements IconEnumstype {
    module("模块", "ri-stack-line"),
    input("输入框", "ri-keyboard-line"),
    list("选择项", "ri-list-check"),
    button("按钮", "ri-checkbox-blank-line"),
    number("数字", "ri-hashtag"),
    other("其他", "ri-more-line");

    private final String name;
    private final String imageClass;

    ComboType(String name, String imageClass) {

        this.name = name;
        this.imageClass = imageClass;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

    @Override
    public String getName() {
        return name;
    }

}
