package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.Enumstype;

public enum DeviceStatus implements Enumstype {


    OFFLINE("OFFLINE", 0, "脱机"),

    ONLINE("ONLINE", 1, "在线"),

    ACTIVATE("ACTIVATE", 2, "激活"),

    FAULT("FAULT", 3, "故障"),

    DELETE("DELETE", 4, "移除"),

    DORMANCY("DORMANCY", 5, "休眠"),

    DISABLE("DISABLE", 6, "不可用"),

    SHARE("SHARE", 7, "SHARE");


    private String type;


    private String name;

    private Integer code;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }


    DeviceStatus(String type, Integer code, String name) {
        this.type = type;
        this.name = name;
        this.code = code;

    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return code.toString();
    }

    public static DeviceStatus fromType(String typeName) {
        for (DeviceStatus type : DeviceStatus.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return DISABLE;
    }

    public static DeviceStatus fromCode(Integer code) {
        for (DeviceStatus type : DeviceStatus.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return DISABLE;
    }


}
