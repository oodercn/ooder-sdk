package net.ooder.annotation.ui;

import net.ooder.annotation.IconEnumstype;

public enum PositionType implements IconEnumstype {
    inner("内部渲染", "ri-rectangle-line"), module("模块", "ri-crop-line"), top("最上层", "ri-layout-row-line");


    private final String name;

    private final String imageClass;

        PositionType(String name, String imageClass) {
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