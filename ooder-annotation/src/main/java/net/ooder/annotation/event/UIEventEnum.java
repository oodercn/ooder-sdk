package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * UI基础组件事件枚举定义
 * 对应JS文件: src/main/js/UI.js
 */
public enum UIEventEnum implements EventKey {
    beforeInputAlert("beforeInputAlert"),
    onContextmenu("onContextmenu"),
    onClick("onClick"),
    onDock("onDock"),
    onLayout("onLayout"),
    onMove("onMove"),
    onRender("onRender"),
    onResize("onResize"),
    onShowTips("onShowTips"),
    beforeHoverEffect("beforeHoverEffect"),
    beforeAppend("beforeAppend"),
    afterAppend("afterAppend"),
    beforeRender("beforeRender"),
    afterRender("afterRender"),
    beforeRemove("beforeRemove"),
    afterRemove("afterRemove"),
    onHotKeydown("onHotKeydown"),
    onHotKeypress("onHotKeypress"),
    onHotKeyup("onHotKeyup");

    private String event;
    private String[] params;

    UIEventEnum(String event, String... args) {
        this.event = event;
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

    @Override
    public String toString() {
        return event;
    }
}