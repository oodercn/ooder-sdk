package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum StacksEventEnum implements EventKey {

    onClick("onClick", "点击时"),
    onDblclick("onDblclick", "双击时"),
    onError("onError", "错误时"),
    beforeLoad("beforeLoad", "加载前"),
    afterLoad("afterLoad", "加载后");

    private String event;
    private String[] params;
    private String name;

    StacksEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    StacksEventEnum(String event, String name, String... args) {
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