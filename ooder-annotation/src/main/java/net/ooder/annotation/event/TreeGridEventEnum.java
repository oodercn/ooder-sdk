package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum TreeGridEventEnum implements EventKey {

    onBodyLayout("onBodyLayout", "主体布局", "profile", "trigger"),
    beforeApplyDataset("beforeApplyDataset", "应用数据集前", "profile", "dataset"),
    beforeCellKeydown("beforeCellKeydown", "单元格按键按下前", "profile", "cell", "keys"),
    afterCellFocused("afterCellFocused", "单元格获取焦点后", "profile", "cell", "row"),


    beforeInitHotRow("beforeInitHotRow", "初始化热行前", "profile"),
    onInitHotRow("onInitHotRow", "初始化热行", "profile", "row"),
    beforeHotRowAdded("beforeHotRowAdded", "添加热行前", "profile", "cellMap", "row", "leaveTreeGrid"),
    afterHotRowAdded("afterHotRowAdded", "添加热行后", "profile", "row"),
    onGetContent("onGetContent", "获取内容", "profile", "row", "callback"),
    onRowSelected("onRowSelected", "行选中", "profile", "row", "e", "src", "type"),
    onCmd("onCmd", "命令执行", "profile", "row", "cmdkey", "e", "src"),
    beforeFold("beforeFold", "折叠前", "profile", "item"),
    beforeExpand("beforeExpand", "展开前", "profile", "item"),
    afterFold("afterFold", "折叠后", "profile", "item"),
    afterExpand("afterExpand", "展开后", "profile", "item"),
    beforeColDrag("beforeColDrag", "列拖拽前", "profile", "colId"),


    beforeColMoved("beforeColMoved", "列移动前", "profile", "colId", "toId"),
    afterColMoved("afterColMoved", "列移动后", "profile", "colId", "toId"),
    beforeColSorted("beforeColSorted", "列排序前", "profile", "col"),
    afterColSorted("afterColSorted", "列排序后", "profile", "col"),
    beforeColShowHide("beforeColShowHide", "列显示隐藏前", "profile", "colId", "flag"),
    afterColShowHide("afterColShowHide", "列显示隐藏后", "profile", "colId", "flag"),
    beforeColResized("beforeColResized", "列调整大小前", "profile", "colId", "width"),
    afterColResized("afterColResized", "列调整大小后", "profile", "colId", "width"),
    beforeRowResized("beforeRowResized", "行调整大小前", "profile", "rowId", "height"),
    afterRowResized("afterRowResized", "行调整大小后", "profile", "rowId", "height"),
    beforePrepareRow("beforePrepareRow", "准备行前", "profile", "row", "pid"),
    beforePrepareCol("beforePrepareCol", "准备列前", "profile", "col"),

    beforeRowActive("beforeRowActive", "行激活前", "profile", "row"),
    afterRowActive("afterRowActive", "行激活后", "profile", "row"),
    beforeCellActive("beforeCellActive", "单元格激活前", "profile", "cell"),
    afterCellActive("afterCellActive", "单元格激活后", "profile", "cell"),
    beforeCellUpdated("beforeCellUpdated", "单元格更新前", "profile", "cell", "options", "isHotRow"),
    afterCellUpdated("afterCellUpdated", "单元格更新后", "profile", "cell", "options", "isHotRow"),
    beforeRowUpdated("beforeRowUpdated", "行更新前", "profile", "obj", "options", "isHotRow"),
    afterRowUpdated("afterRowUpdated", "行更新后", "profile", "obj", "options", "isHotRow"),
    onRowDirtied("onRowDirtied", "行脏标记", "profile", "row"),
    onRowHover("onRowHover", "行悬停", "profile", "row", "hover", "e", "src"),
    onClickHeader("onClickHeader", "点击表头", "profile", "col", "e", "src"),
    onClickRow("onClickRow", "点击行", "profile", "row", "e", "src"),

    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    onClickRowHandler("onClickRowHandler", "点击行处理器", "profile", "row", "e", "src"),
    onDblclickRow("onDblclickRow", "双击行", "profile", "row", "e", "src"),
    onClickCell("onClickCell", "点击单元格", "profile", "cell", "e", "src"),
    onDblclickCell("onDblclickCell", "双击单元格", "profile", "cell", "e", "src"),
    onClickTreeGridHandler("onClickTreeGridHandler", "点击网格处理器", "profile", "e", "src"),
    beforeIniEditor("beforeIniEditor", "初始化编辑器前", "profile", "cell", "cellNode", "pNode", "type"),
    onBeginEdit("onBeginEdit", "开始编辑", "profile", "cell", "editor", "type"),
    beforeEditApply("beforeEditApply", "应用编辑前", "profile", "cell", "options", "editor", "tag", "type"),
    onEndEdit("onEndEdit", "结束编辑", "profile", "cell", "editor", "type"),


    onDrop("onDrop", "拖放", "profile", "e", "src"),
    onGetDragData("onGetDragData", "获取拖拽数据", "profile", "e", "src"),
    onStartDrag("onStartDrag", "开始拖拽", "profile", "e", "src"),
    onDragEnter("onDragEnter", "拖拽进入", "profile", "e", "src"),


    onDragLeave("onDragLeave", "拖拽离开", "profile", "e", "src"),
    onDragStop("onDragStop", "拖拽停止", "profile", "e", "src"),
    onDropTest("onDropTest", "拖放测试", "profile", "e", "src"),

    beforeDrop("beforeDrop", "拖放前", "profile", "e", "src"),
    onDropMarkShow("onDropMarkShow", "显示拖放标记", "profile", "e", "src"),
    afterDrop("afterDrop", "拖放后", "profile", "e", "src"),


    onFileDlgOpen("onFileDlgOpen", "文件对话框打开", "profile", "cell", "proEditor", "src"),
    beforeComboPop("beforeComboPop", "组合弹出前", "profile", "cell", "proEditor", "pos", "e", "src"),
    beforePopShow("beforePopShow", "弹出显示前", "profile", "cell", "proEditor", "popCtl", "items"),


    pan("pan", "平移", "profile", "item", "e", "src"),
    panstart("panstart", "平移开始", "profile", "item", "e", "src"),
    panmove("panmove", "平移移动", "profile", "item", "e", "src"),
    panend("panend", "平移结束", "profile", "item", "e", "src"),
    pancancel("pancancel", "平移取消", "profile", "item", "e", "src"),
    panleft("panleft", "向左平移", "profile", "item", "e", "src"),
    panright("panright", "向右平移", "profile", "item", "e", "src"),
    panup("panup", "向上平移", "profile", "item", "e", "src"),
    pandown("pandown", "向下平移", "profile", "item", "e", "src"),

    pinch("pinch", "捏合", "profile", "item", "e", "src"),
    pinchstart("pinchstart", "捏合开始", "profile", "item", "e", "src"),
    pinchmove("pinchmove", "捏合移动", "profile", "item", "e", "src"),
    pinchend("pinchend", "捏合结束", "profile", "item", "e", "src"),
    pinchcancel("pinchcancel", "捏合取消", "profile", "item", "e", "src"),
    pinchin("pinchin", "捏合内缩", "profile", "item", "e", "src"),
    pinchout("pinchout", "捏合外扩", "profile", "item", "e", "src"),

    rotate("rotate", "旋转", "profile", "item", "e", "src"),
    rotatestart("rotatestart", "旋转开始", "profile", "item", "e", "src"),
    rotatemove("rotatemove", "旋转移动", "profile", "item", "e", "src"),
    rotateend("rotateend", "旋转结束", "profile", "item", "e", "src"),
    rotatecancel("rotatecancel", "旋转取消", "profile", "item", "e", "src"),

    press("press", "按下", "profile", "item", "e", "src"),
    pressup("pressup", "抬起", "profile", "item", "e", "src"),

    swipe("swipe", "滑动", "profile", "item", "e", "src"),
    swipeleft("swipeleft", "向左滑动", "profile", "item", "e", "src"),
    swiperight("swiperight", "向右滑动", "profile", "item", "e", "src"),
    swipeup("swipeup", "向上滑动", "profile", "item", "e", "src"),
    swipedown("swipedown", "向下滑动", "profile", "item", "e", "src"),


    afterPopShow("afterPopShow", "弹出显示后", "profile", "cell", "proEditor", "popCtl"),
    onCommand("onCommand", "命令执行", "profile", "cell", "proEditor", "src", "type"),
    onEditorClick("onEditorClick", "编辑器点击", "profile", "cell", "proEditor", "type", "src"),
    beforeUnitUpdated("beforeUnitUpdated", "单位更新前", "profile", "cell", "proEditor", "type"),
    beforeApplyFormula("beforeApplyFormula", "应用公式前", "profile", "cell", "value", "formula"),
    afterApplyFormulas("afterApplyFormulas", "应用公式后", "profile", "dataArrs"),
    beforeTreeGridValueCalculated("beforeTreeGridValueCalculated", "网格值计算前", "profile"),
    afterTreeGridValueCalculated("afterTreeGridValueCalculated", "网格值计算后", "profile", "value"),
    onGetExcelCellValue("onGetExcelCellValue", "获取Excel单元格值", "profile", "excelCellId", "dftValue");

    private String event;
    private String[] params;
    private String name;

    TreeGridEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    TreeGridEventEnum(String event, String name, String... args) {
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