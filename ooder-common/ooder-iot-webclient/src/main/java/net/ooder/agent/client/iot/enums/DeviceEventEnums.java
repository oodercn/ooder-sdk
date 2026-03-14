package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.EventEnums;

public enum DeviceEventEnums implements EventEnums {

    register("注册成功", "register"),

    deviceActivt("设备重新激活", "deviceActivt"),

    deleteing("设备开始移除", "deleteing"),

    deleteFail("移除失败", "deleteFail"),

    areaUnBind("解除绑定", "areaUnBind"),

    areaBind("绑定", "areaBind"),

    onLine("设备上线", "onLine"),

    offLine("设备离线", "offLine");


    private String name;


    private String method;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    DeviceEventEnums(String name, String method) {

        this.name = name;
        this.method = method;

    }

    @Override
    public String toString() {
        return method.toString();
    }


    public static DeviceEventEnums fromMethod(String method) {
        for (DeviceEventEnums type : DeviceEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static DeviceEventEnums fromType(String method) {
        for (DeviceEventEnums type : DeviceEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return method.toString();
    }

}
