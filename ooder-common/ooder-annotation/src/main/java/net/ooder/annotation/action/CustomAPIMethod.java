package net.ooder.annotation.action;


import net.ooder.annotation.Enumstype;

public enum CustomAPIMethod implements Enumstype {
    invoke("invoke", "调用"),
    destroy("destroy", "销毁"),
    setQueryData("setQueryData", "设置查询参数");

    private final String type;
    private final String name;

    CustomAPIMethod(String type, String name) {
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
