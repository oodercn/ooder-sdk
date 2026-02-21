package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum DivEventEnum implements EventKey {
    onRender("onRender", "渲染时", "profile"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    onClick("onClick", "点击时", "profile", "e", "src");

    private String event;
    private String[] params;
    private String name;

    DivEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    DivEventEnum(String event, String name, String... args) {
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