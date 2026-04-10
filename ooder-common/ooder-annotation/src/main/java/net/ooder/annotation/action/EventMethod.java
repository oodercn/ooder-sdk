package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum EventMethod implements Enumstype {
    getId("getId", "获取事件ID", "src"),
    getKey("getKey", "获取事件键", "id"),
    getSrc("getSrc", "获取事件源", "event"),
    stopBubble("stopBubble", "停止事件冒泡", "event"),
    stopDefault("stopDefault", "阻止默认行为", "event"),
    addEventHook("addEventHook", "添加事件钩子", "fun", "type", "priority"),
    removeEventHook("removeEventHook", "移除事件钩子", "fun", "type"),
    setBlurTrigger("setBlurTrigger", "设置失焦触发器", "id", "fun", "arr"),
    unsetBlurTrigger("unsetBlurTrigger", "取消失焦触发器", "id");

    private final String type;
    private final String name;
    private final String[] parameters;

    EventMethod(String type, String name, String... parameters) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return parameters;
    }
}