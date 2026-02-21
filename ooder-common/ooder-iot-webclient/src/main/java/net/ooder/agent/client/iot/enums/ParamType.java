package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.Enumstype;

public enum ParamType implements Enumstype {

    HA("HA", "HA属性"),

    App("App", "应用扩展"),

    Factory("Factory", "厂商扩展"),

    System("System", "系统扩展"),

    Other("Other", "其他"),

    Z3("Z3", "zigbee3.0属性");



    private String type;

    private String name;


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    ParamType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return type;
    }

    public static ParamType fromType(String typeName) {
        for (ParamType type : ParamType.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return Other;
    }

}
