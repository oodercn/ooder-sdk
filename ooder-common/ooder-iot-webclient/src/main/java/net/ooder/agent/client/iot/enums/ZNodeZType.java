package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.Enumstype;

public enum ZNodeZType implements Enumstype {

    GATEWAY("GATEWAY", "网关"),


    SHARE("SHARE", "虚拟共享节点"),

    Light("Light", "灯光控制"),

    Level("Level", "调级"),

    Scene("Scene", "场景应用"),

    Zone("Zone", "Zone设备"),

    Lock("Lock", "门锁"),

    Power("Power", "电气"),

    Sensor("Sensor", "传感器设备"),

    IRLearn("IRLearn", "紅外学习"),

    Other("Other", "Other"),

    IRControl("IRControl", "紅外控制");


    private String type;

    private String name;


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    ZNodeZType(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type.toString();
    }

    public static ZNodeZType fromType(String typeName) {
        if (typeName != null) {
            for (ZNodeZType type : ZNodeZType.values()) {
                if (type.getType() != null && type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                    return type;
                }
            }
        }

        return Other;
    }


}
