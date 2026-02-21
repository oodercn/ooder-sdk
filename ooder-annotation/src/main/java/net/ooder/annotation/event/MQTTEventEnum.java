package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * MQTT组件事件枚举定义
 * 对应JS文件: src/main/js/MQTT.js
 */
public enum MQTTEventEnum implements EventKey {
    onConnSuccess("onConnSuccess", "连接成功", "profile", "reconnect"),
    onConnFailed("onConnFailed", "连接失败", "profile", "error"),
    onConnLost("onConnLost", "连接丢失", "profile", "error"),
    onSubSuccess("onSubSuccess", "订阅成功", "profile", "topic"),
    onSubFailed("onSubFailed", "订阅失败", "profile", "error", "topic"),
    onUnsubSuccess("onUnsubSuccess", "取消订阅成功", "profile", "topic"),
    onUnsubFailed("onUnsubFailed", "取消订阅失败", "profile", "error", "topic"),
    onMsgDelivered("onMsgDelivered", "消息送达", "profile", "payloadString", "msgObj"),
    onMsgArrived("onMsgArrived", "消息到达", "profile", "payloadString", "msgObj", "playloadObj");

    private String event;
    private String[] params;
    private String name;

    MQTTEventEnum(String event, String name, String... args) {
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