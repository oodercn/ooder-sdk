package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum HotKeyEventEnum implements EventKey {

    onHotKeydown("onHotKeydown", "热键按下", "profile", "key", "e", "src"),
    onHotKeypress("onHotKeypress", "热键按下时", "profile", "key", "e", "src"),
    onHotKeyup("onHotKeyup", "热键释放", "profile", "key", "e", "src");

    private String event;
    private String[] params;
    private String name;

    HotKeyEventEnum(String event, String name, String... args) {
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