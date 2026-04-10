package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum ResizerMethod implements Enumstype {
    show("show", "显示"),
    hide("hide", "隐藏");

    private final String type;
    private final String name;
    private final String[] parameters;

    ResizerMethod(String type, String name, String... parameters) {
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