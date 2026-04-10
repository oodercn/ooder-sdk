package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum DragDropMethod implements Enumstype {
    getProfile("getProfile", "获取配置信息");

    private final String type;
    private final String name;
    private final String[] parameters;

    DragDropMethod(String type, String name, String... parameters) {
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