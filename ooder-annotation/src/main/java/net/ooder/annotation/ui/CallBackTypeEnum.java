package net.ooder.annotation.ui;


import net.ooder.annotation.Enumstype;

public enum CallBackTypeEnum implements Enumstype {


    postMessage("postMessage", "发送消息"),

    destroy("destroy", "销毁当前模块"),

    reloadParent("reloadParent", "刷新父级"),

    destroyCurrDio("destroyCurrDio", "销毁当前窗口"),

    destroyParent("destroyParent", "销毁父级"),

    initData("initData", "刷新页面"),

    log("log", "日志"),

    alert("alert", "alert");


    private final ComponentType[] componentTypes;

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    CallBackTypeEnum(String type, String name, ComponentType... componentTypes) {
        this.type = type;
        this.name = name;
        this.componentTypes = componentTypes;

    }


    @Override
    public String toString() {
        return type;
    }

    public static CallBackTypeEnum fromType(String typeName) {
        for (CallBackTypeEnum type : CallBackTypeEnum.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
