package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * ComboInput组件事件枚举定义
 * 对应JS文件: src/main/js/UI/ComboInput.js
 */
public enum ComboInputEventEnum implements EventKey {
    onFileDlgOpen("onFileDlgOpen", "文件对话框打开", "profile", "src"),
    onCommand("onCommand", "命令执行", "profile", "src", "type"),
    beforeComboPop("beforeComboPop", "组合弹出前", "profile", "pos", "e", "src"),
    beforePopShow("beforePopShow", "弹出显示前", "profile", "popCtl", "items"),
    afterPopShow("afterPopShow", "弹出显示后", "profile", "popCtl"),
    afterPopHide("afterPopHide", "弹出隐藏后", "profile", "popCtl", "type"),
    onClick("onClick", "点击", "profile", "e", "src", "btn", "value"),
    onClickIcon("onClickIcon", "图标点击", "profile", "src"),
    beforeUnitUpdated("beforeUnitUpdated", "单位更新前", "profile", "unit"),
    afterUnitUpdated("afterUnitUpdated", "单位更新后", "profile", "unit");

    private String event;
    private String[] params;
    private String name;

    ComboInputEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    @Override
    public String getEvent() {
        return event;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return event;
    }
}