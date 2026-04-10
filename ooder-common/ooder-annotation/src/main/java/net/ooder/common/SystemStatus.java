package net.ooder.common;

import net.ooder.annotation.Enumstype;

public enum SystemStatus implements Enumstype {


    OFFLINE("OFFLINE", "离线"),

    ONLINE("ONLINE", "在线"),

    FAULT("FAULT", "故障"),

    DELETE("DELETE", "移除"),

    DISABLE("DISABLE", "不可用");

    private String type;


    private String name;


    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }


    SystemStatus(String type, String name) {
        this.type = type;
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

    public static SystemStatus fromType(String typeName) {
        for (SystemStatus type : SystemStatus.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return DISABLE;
    }


}
