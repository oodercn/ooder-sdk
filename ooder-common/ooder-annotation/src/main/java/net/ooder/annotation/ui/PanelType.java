package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum PanelType implements Enumstype {

    panel("普通面板"), dialog("对话框"), block("程序块"), div("通用图层");

    private final String name;


    PanelType(String name) {
        this.name = name;
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
    public String getName() {
        return name;
    }

}
