package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Mobile Card事件枚举定义
 * 对应JS中ood.Mobile.Card组件的EventHandlers定义
 */
public enum MobileCardEventEnum implements EventKey {
    onCardClick("onCardClick", "卡片点击", "profile", "event"),
    onActionClick("onActionClick", "动作点击", "profile", "action", "index", "event");

    private String event;
    private String[] params;
    private String name;

    MobileCardEventEnum(String event, String name, String... args) {
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