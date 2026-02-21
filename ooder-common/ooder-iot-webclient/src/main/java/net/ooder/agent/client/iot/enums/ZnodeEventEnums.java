package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.EventEnums;

public enum ZnodeEventEnums implements EventEnums {

    sensorCreated("创建成功", "sensorCreated"),

    znodeMoved("移除成功", "znodeMoved"),

    sensorLocked("锁定", "sensorLocked"),

    sensorUnLocked("解锁", "sensorUnLocked"),


    sceneAdded("添加场景", "sceneAdded"),

    sceneRemoved("移除场景", "sceneRemoved");


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

    ZnodeEventEnums(String name, String method) {

        this.name = name;
        this.method = method;


    }

    @Override
    public String toString() {
        return method.toString();
    }


    public static ZnodeEventEnums fromMethod(String method) {
        for (ZnodeEventEnums type : ZnodeEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static ZnodeEventEnums fromType(String method) {
        for (ZnodeEventEnums type : ZnodeEventEnums.values()) {
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
