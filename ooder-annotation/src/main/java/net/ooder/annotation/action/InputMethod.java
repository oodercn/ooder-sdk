package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum InputMethod implements Enumstype {
    // 从父类ood.UI.Widget继承的方法
    setWidth("setWidth", "设置宽度", "width"),
    setHeight("setHeight", "设置高度", "height"),
    setLeft("setLeft", "设置左边距", "left"),
    setTop("setTop", "设置上边距", "top"),
    setPosition("setPosition", "设置位置", "position"),
    setZIndex("setZIndex", "设置层级", "zIndex"),
    show("show", "显示"),
    hide("hide", "隐藏"),
    toggleVisible("toggleVisible", "切换可见性"),
    setDisabled("setDisabled", "设置禁用状态", "disabled"),
    setReadonly("setReadonly", "设置只读状态", "readonly"),
    setClass("setClass", "设置CSS类", "className"),
    addClass("addClass", "添加CSS类", "className"),
    removeClass("removeClass", "移除CSS类", "className"),
    toggleClass("toggleClass", "切换CSS类", "className"),
    hasClass("hasClass", "检查是否有CSS类", "className"),
    setStyle("setStyle", "设置样式", "style"),
    setAttr("setAttr", "设置属性", "name", "value"),
    getAttr("getAttr", "获取属性", "name"),
    removeAttr("removeAttr", "移除属性", "name"),
    setTheme("setTheme", "设置主题", "theme"),
    getTheme("getTheme", "获取主题"),
    toggleDarkMode("toggleDarkMode", "切换暗黑模式"),
    
    // 从父类ood.absValue继承的方法
    getValue("getValue", "获取值", "returnArr"),
    setValue("setValue", "设置值", "value", "force", "tag", "triggerEventOnly"),
    getUIValue("getUIValue", "获取UI值", "returnArr"),
    setUIValue("setUIValue", "设置UI值", "value", "force", "triggerEventOnly", "tag"),
    resetValue("resetValue", "重置值", "value"),
    updateValue("updateValue", "更新值"),
    isDirtied("isDirtied", "是否已修改"),
    checkValid("checkValid", "检查有效性", "value"),
    getSelectedItem("getSelectedItem", "获取选中项目", "returnArr"),
    
    // Input组件自身的方法
    activate("activate", "激活", "select"),
    getAutoexpandHeight("getAutoexpandHeight", "获取自动扩展高度"),
    notifyExcel("notifyExcel", "通知Excel", "refreshAll"),
    getExcelCellValue("getExcelCellValue", "获取Excel单元格值");

    private final String type;
    private final String name;
    private final String[] parameters;

    InputMethod(String type, String name, String... parameters) {
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