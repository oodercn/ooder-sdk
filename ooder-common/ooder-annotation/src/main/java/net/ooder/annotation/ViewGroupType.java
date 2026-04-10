package net.ooder.annotation;

import net.ooder.annotation.Enumstype;

public enum ViewGroupType implements Enumstype {
    CHARTS("绘图图表", "ri-bar-chart-line"),
    MOBILE("移动组件", "ri-phone-line"),
    MODULE("高级组件", "ri-settings-3-line"),
    DIC("字典辅助", "ri-book-line"),
    LAYOUT("布局容器", "ri-layout-line"),
    VIEW("通用视图", "ri-eye-line"),
    NAV("框架导航", "ri-compass-line"),
    NAVTREE("菜单导航", "ri-node-tree");


    private final String name;
    private final String imageClass;


    ViewGroupType(String name, String imageClass) {
        this.name = name;
        this.imageClass = imageClass;
    }


    public String getImageClass() {
        return imageClass;
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