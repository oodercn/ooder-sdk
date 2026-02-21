package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum ComboInputMethod implements Enumstype {
    // 从父类ood.UI.Input继承的方法 (ood.UI.Widget方法)
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
    
    // 从父类ood.UI.Input继承的方法 (ood.absValue方法)
    getValue("getValue", "获取值"),
    setValue("setValue", "设置值", "value", "force", "tag", "triggerEventOnly"),
    getUIValue("getUIValue", "获取UI值"),
    setUIValue("setUIValue", "设置UI值", "value", "force", "triggerEventOnly", "tag"),
    resetValue("resetValue", "重置值", "value"),
    updateValue("updateValue", "更新值"),
    isDirtied("isDirtied", "是否已修改"),
    checkValid("checkValid", "检查有效性", "value"),
    getSelectedItem("getSelectedItem", "获取选中项目", "returnArr"),
    getAllSelectedValues("getAllSelectedValues", "获取所有选中值", "key"),
    getUICationValue("getUICationValue", "获取UI标题值", "returnArr"),
    getCaptionValue("getCaptionValue", "获取标题值"),
    setCaptionValue("setCaptionValue", "设置标题值", "value"),
    getShowValue("getShowValue", "获取显示值", "value"),
    setPopWnd("setPopWnd", "设置弹出窗口", "drop"),
    clearPopCache("clearPopCache", "清空弹出缓存"),
    setUploadObj("setUploadObj", "设置上传对象", "input"),
    getUploadObj("getUploadObj", "获取上传对象"),
    popFileSelector("popFileSelector", "弹出文件选择器", "accept", "multiple"),
    adjustLayout("adjustLayout", "响应式布局调整"),
    enhanceAccessibility("enhanceAccessibility", "增强可访问性支持"),
    ComboInputTrigger("ComboInputTrigger", "组合输入触发器"),
    activate("activate", "激活", "select"),
    getAutoexpandHeight("getAutoexpandHeight", "获取自动扩展高度"),
    notifyExcel("notifyExcel", "通知Excel", "refreshAll"),
    getExcelCellValue("getExcelCellValue", "获取Excel单元格值");

    private final String type;
    private final String name;
    private final String[] parameters;

    ComboInputMethod(String type, String name, String... parameters) {
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