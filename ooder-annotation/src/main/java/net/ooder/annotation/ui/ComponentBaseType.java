package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum ComponentBaseType implements Enumstype {
    absValue("表单组件", "ood.absValue"), absObj("普通组件", "ood.absObj"), Widget("独立控件", "ood.UI.Widget"),
    absList("集合组件", "ood.absList"), Div("层组件", "ood.UI.Div"), absProfile("Module组件", "ood.absProfile"), svg("SVG图形", "ood.svg"), absComb("组合图形", "ood.svg.absComb");

    private final String name;
    private final String className;

    public String getClassName() {
        return className;
    }

    ComponentBaseType(String name, String className) {
        this.name = name;
        this.className = className;
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
