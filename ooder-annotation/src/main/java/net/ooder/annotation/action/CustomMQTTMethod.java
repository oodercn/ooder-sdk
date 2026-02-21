package net.ooder.annotation.action;


import net.ooder.annotation.Enumstype;

public enum CustomMQTTMethod implements Enumstype {
    connect("connect", "连接"),
    disconnect("disconnect", "断开"),
    subscribe("subscribe", "订阅"),
    unsubscribe("unsubscribe", "取消订阅"),
    destroy("destroy", "销毁"),
    publish("publish", "发布"),
    setProperties("setProperties", "设置属性");
    private final String type;
    private final String name;

    CustomMQTTMethod(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
