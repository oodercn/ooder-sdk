package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Input组件事件枚举
 * 对应JS中的ood.UI.Input.EventHandlers定义
 */
public enum InputEventEnum implements EventKey {
    // 输入框获得焦点前事件
    beforeFocus("beforeFocus", "获得焦点前", "profile"),
    
    // 输入框获得焦点事件
    onFocus("onFocus", "获得焦点时", "profile"),
    
    // 输入框失去焦点事件
    onBlur("onBlur", "失去焦点时", "profile"),
    
    // 取消事件
    onCancel("onCancel", "取消时", "profile"),
    
    // 格式检查前事件
    beforeFormatCheck("beforeFormatCheck", "格式检查前", "profile", "value"),
    
    // 格式标记前事件
    beforeFormatMark("beforeFormatMark", "格式标记前", "profile", "formatErr"),
    
    // 按键按下前事件
    beforeKeypress("beforeKeypress", "按键按下前", "profile", "caret", "keyboard", "e", "src"),
    
    // 标签点击事件
    onLabelClick("onLabelClick", "标签点击", "profile", "e", "src"),
    
    // 标签双击事件
    onLabelDblClick("onLabelDblClick", "标签双击", "profile", "e", "src"),
    
    // 标签激活事件
    onLabelActive("onLabelActive", "标签激活", "profile", "e", "src"),
    
    // 获取Excel单元格值事件
    onGetExcelCellValue("onGetExcelCellValue", "获取Excel单元格值", "profile", "excelCellId", "dftValue"),
    
    // 应用Excel公式前事件
    beforeApplyExcelFormula("beforeApplyExcelFormula", "应用Excel公式前", "profile", "excelCellFormula", "value"),
    
    // 应用Excel公式后事件
    afterApplyExcelFormula("afterApplyExcelFormula", "应用Excel公式后", "profile", "excelCellFormula", "value"),
    
    // 自动扩展事件
    onAutoexpand("onAutoexpand", "自动扩展", "profile", "height", "offset"),
    
    // 父类事件
    beforeRender("beforeRender", "渲染前", "profile"),
    onRender("onRender", "渲染时", "profile"),
    onLayout("onLayout", "布局时", "profile"),
    onResize("onResize", "调整大小", "profile", "width", "height"),
    onMove("onMove", "移动时", "profile", "left", "top", "right", "bottom"),
    onDock("onDock", "停靠时", "profile", "region"),
    beforePropertyChanged("beforePropertyChanged", "属性变更前", "profile", "name", "value", "ovalue"),
    afterPropertyChanged("afterPropertyChanged", "属性变更后", "profile", "name", "value", "ovalue"),
    beforeAppend("beforeAppend", "添加前", "profile", "child"),
    afterAppend("afterAppend", "添加后", "profile", "child"),
    beforeRemove("beforeRemove", "移除前", "profile", "child", "subId", "bdestroy"),
    afterRemove("afterRemove", "移除后", "profile", "child", "subId", "bdestroy"),
    onDestroy("onDestroy", "销毁时", "profile"),
    beforeDestroy("beforeDestroy", "销毁前", "profile"),
    afterDestroy("afterDestroy", "销毁后", "profile"),
    onShowTips("onShowTips", "显示提示", "profile", "node", "pos"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos");

    private String event;
    private String[] params;
    private String name;

    InputEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public String getName() {
        return name;
    }
}