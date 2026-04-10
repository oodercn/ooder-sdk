package net.ooder.annotation.ui;


import net.ooder.annotation.CaptionEnumstype;

public enum ItemColorEnum implements CaptionEnumstype {
    NONE("", "默认"),
    YELLOW("#F19B60", "黄色"),
    GREEN("#49CD81", "绿色"),
    DARKBLUE("#0277bd", "深蓝"),
    LIGHTPURPLE("#A693EB", "紫色"),
    CYAN("#42a3af", "青色"),
    BABYBLUE("#B0E0E6", "浅蓝"),
    PINK("#F06292", "桃红");

    String name;

    String caption;

    ItemColorEnum(String name, String caption) {
        this.name = name;
        this.caption = caption;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getType() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCaption() {
        return caption;
    }

}
