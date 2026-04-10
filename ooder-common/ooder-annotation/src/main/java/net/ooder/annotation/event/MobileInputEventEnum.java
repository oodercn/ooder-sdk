package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * 移动端Input组件事件枚举定义
 * 对应JS文件: src/main/js/UI.mobile.js
 */
public enum MobileInputEventEnum implements EventKey {
    onChange("onChange", "变更时", "profile", "e", "value");

    private String event;
    private String[] params;
    private String name;

    MobileInputEventEnum(String event, String name, String... args) {
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