package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Resizer组件事件枚举定义
 * 对应JS文件: src/main/js/UI/Resizer.js
 */
public enum ResizerEventEnum implements EventKey {
    onDblclick("onDblclick", "双击时", "profile", "e", "src"),
    onUpdate("onUpdate", "更新时", "profile", "target", "size", "cssPos", "rotate"),
    onChange("onChange", "变更时", "profile", "proxy"),
    onConfig("onConfig", "配置时", "profile", "e", "src", "pos", "type");

    private String event;
    private String[] params;
    private String name;

    ResizerEventEnum(String event, String name, String... args) {
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