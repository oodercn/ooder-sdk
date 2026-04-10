package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum MessageEventEnum implements EventKey {

    onMessageReceived("onMessageReceived", "消息接收", "profile", "msg1", "msg2", "msg3", "msg4", "msg5", "readReceipt"),
    onReceipt("onReceipt", "回执", "profile", "recipientType", "args");

    private String event;
    private String[] params;
    private String name;

    MessageEventEnum(String event, String name, String... args) {
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