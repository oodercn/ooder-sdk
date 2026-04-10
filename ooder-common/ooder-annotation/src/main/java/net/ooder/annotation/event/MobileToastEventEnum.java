package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Mobile Toast事件枚举定义
 * 对应JS中ood.Mobile.Toast组件的EventHandlers定义
 */
public enum MobileToastEventEnum implements EventKey {
    onHide("onHide", "隐藏时", "profile");

    private String event;
    private String[] params;
    private String name;

    MobileToastEventEnum(String event, String name, String... args) {
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