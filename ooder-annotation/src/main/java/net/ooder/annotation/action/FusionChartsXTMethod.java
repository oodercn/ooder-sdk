package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum FusionChartsXTMethod implements Enumstype {
    initialize("initialize", "初始化"),
    refreshChart("refreshChart", "刷新图表", "dataFormat"),
    setTransparent("setTransparent", "设置透明度", "isTransparent"),
    getChartAttribute("getChartAttribute", "获取图表属性", "key"),
    setChartAttribute("setChartAttribute", "设置图表属性", "key", "value"),
    getFCObject("getFCObject", "获取FC对象"),
    getSVGString("getSVGString", "获取SVG字符串"),
    updateCategories("updateCategories", "更新分类", "data", "index"),
    updateLine("updateLine", "更新线条", "data", "index"),
    updateData("updateData", "更新数据", "data", "index"),
    fillData("fillData", "填充数据", "data", "index", "isLineset");

    private final String type;
    private final String name;
    private final String[] parameters;

    FusionChartsXTMethod(String type, String name, String... parameters) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return parameters;
    }
}