package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum FieldEventEnum implements EventKey {

    beforeFocus("beforeFocus", "获取焦点前", "profile"),
    onFocus("onFocus", "获取焦点时", "profile"),
    onBlur("onBlur", "失去焦点时", "profile"),
    onCancel("onCancel", "取消时", "profile"),

    onClick("onClick", "点击时", "profile", "e", "src"),
   // onChange("onChange", "变更时", "profile", "oldValue", "newValue"),

    //image
   // onDblclick("onDblclick", "双击时", "profile", "e", "src"),
    onError("onError", "错误时", "profile"),
    beforeLoad("beforeLoad", "加载前", "profile"),
    afterLoad("afterLoad", "加载后", "profile", "path", "width", "height"),


    onFileDlgOpen("onFileDlgOpen", "文件对话框打开", "profile", "src"),
    beforeComboPop("beforeComboPop", "组合弹出前", "profile", "pos", "e", "src"),
    afterPopShow("afterPopShow", "弹出显示后", "profile", "popCtl"),
    afterPopHide("afterPopHide", "弹出隐藏后", "profile", "popCtl", "type"),
    onClickIcon("onClickIcon", "图标点击", "profile", "src"),
    beforeUnitUpdated("beforeUnitUpdated", "单位更新前", "profile", "unit"),
    onCommand("onCommand", "命令执行", "profile", "src", "type"),


    beforeFormatCheck("beforeFormatCheck", "格式检查前", "profile", "value"),
    beforeFormatMark("beforeFormatMark", "格式标记前", "profile", "formatErr"),
    beforeKeypress("beforeKeypress", "按键按下前", "profile", "caret", "keyboard", "e", "src"),
    afterUnitUpdated("afterUnitUpdated", "单位更新后", "profile", "unit"),

    //onLabelDblClick("onLabelDblClick", "标签双击", "profile", "e", "src"),
    //onLabelActive("onLabelActive", "标签激活", "profile", "e", "src"),
    onGetExcelCellValue("onGetExcelCellValue", "获取Excel单元格值", "profile", "excelCellId", "dftValue"),
    beforeApplyExcelFormula("beforeApplyExcelFormula", "应用Excel公式前", "profile", "excelCellFormula", "value"),


    //list
    onListClick("onClick", "点击时", "profile", "item", "e", "src"),
    onCmd("onCmd", "命令执行", "profile", "item", "cmdkey", "e", "src"),
    onChange("onChange", "变更时", "profile", "oldValue", "newValue", "force", "tag"),
   // onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    beforeClick("beforeClick", "点击前", "profile", "item", "e", "src"),
    afterClick("afterClick", "点击后", "profile", "item", "e", "src"),

    onDblclick("onDblclick", "双击时", "profile", "item", "e", "src"),
    onShowOptions("onShowOptions", "显示选项", "profile", "item", "e", "src"),
    onItemSelected("onItemSelected", "项目选中", "profile", "item", "e", "src", "type"),
   // onLabelClick("onLabelClick", "标签点击", "profile", "e", "src"),

    onLabelDblClick("onLabelDblClick", "标签双击", "profile", "e", "src"),
    onLabelActive("onLabelActive", "标签激活", "profile", "e", "src"),


    //button
    onClickDrop("onClickDrop", "点击下拉", "profile", "e", "src", "value"),
    onChecked("onChecked", "选中时", "profile", "e", "value"),


    onHotKeydown("onHotKeydown", "热键按下", "profile", "key", "e", "src"),
    onHotKeypress("onHotKeypress", "热键按下时", "profile", "key", "e", "src"),
    onHotKeyup("onHotKeyup", "热键抬起", "profile", "key", "e", "src"),


    //FusionChartEvent
    onFusionChartsEvent("onFusionChartsEvent", "FusionCharts事件", "profile", "eventType", "params"),
    onDataClick("onDataClick", "数据点击", "profile", "data"),
    onAnnotationClick("onAnnotationClick", "注解点击", "profile", "annotation"),
    onShowTips("onShowTips", "显示提示", "profile", "node", "pos"),
    onMediaEvent("onMediaEvent", "媒体事件", "profile", "eventType", "params"),


    afterApplyExcelFormula("afterApplyExcelFormula", "应用Excel公式后", "profile", "excelCellFormula", "value"),
    onAutoexpand("onAutoexpand", "自动展开", "profile", "height", "offset"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    onLabelClick("onLabelClick", "标签点击", "profile", "e", "src");

    private String event;
    private String[] params;
    private String name;

    FieldEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    FieldEventEnum(String event, String name, String... args) {
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

    @Override
    public String toString() {
        return event;
    }
}