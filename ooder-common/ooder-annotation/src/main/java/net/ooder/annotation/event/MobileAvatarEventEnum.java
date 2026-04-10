package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Mobile Avatar事件枚举定义
 * 对应JS中ood.Mobile.Avatar组件的EventHandlers定义
 */
public enum MobileAvatarEventEnum implements EventKey {
    onAvatarClick("onAvatarClick", "头像点击", "profile", "event"),
    onImageError("onImageError", "图片错误", "profile", "event"),
    onImageLoad("onImageLoad", "图片加载", "profile", "event");

    private String event;
    private String[] params;
    private String name;

    MobileAvatarEventEnum(String event, String name, String... args) {
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