package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum DatePickerEventEnum implements EventKey {
    beforeClose("beforeClose", "关闭前");

    private String event;
    private String[] params;
    private String name;

    DatePickerEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    DatePickerEventEnum(String event, String name, String... args) {
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