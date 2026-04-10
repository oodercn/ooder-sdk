package net.ooder.annotation.ui;

import net.ooder.annotation.Enumstype;

public enum IconColorEnum implements Enumstype {
    NONE("","默认"),
    YELLOW("#E6945C","黄色"),
    GREEN("#46C37B","绿色"),
    DARKBLUE("#195ead","深蓝"),
    LIGHTPURPLE("#9E8CE0","紫色"),
    CYAN("#1f8d9b","青色"),
    BABYBLUE("#87CEEB","浅蓝"),
    PINK("#e04d7f","桃红"),
    GREY("#b8b8b8","亮灰") ;


    String name;

    String caption;

    IconColorEnum(String name, String caption) {
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


}
