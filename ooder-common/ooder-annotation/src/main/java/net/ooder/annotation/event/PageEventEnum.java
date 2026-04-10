package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum PageEventEnum implements EventKey {

    onPageSet("onPageSet", "页面设置", "profile"),
    onClick("onClick", "点击时", "profile", "e", "src");

    private String event;
    private String[] params;
    private String name;

    PageEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    PageEventEnum(String event, String name, String... args) {
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