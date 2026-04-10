package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Mobile ActionSheet事件枚举定义
 * 对应JS中ood.Mobile.ActionSheet组件的EventHandlers定义
 */
public enum MobileActionSheetEventEnum implements EventKey {
    onShow("onShow", "显示时", "profile"),
    onClose("onClose", "关闭时", "profile"),
    onActionClick("onActionClick", "动作点击", "profile", "action", "index"),
    onCancel("onCancel", "取消时", "profile");

    private String event;
    private String[] params;
    private String name;

    MobileActionSheetEventEnum(String event, String name, String... args) {
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