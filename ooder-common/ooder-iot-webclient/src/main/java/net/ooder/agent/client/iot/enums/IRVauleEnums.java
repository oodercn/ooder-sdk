package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.Enumstype;

public enum IRVauleEnums implements Enumstype {

    OPEN("0", "空调开机"),

    CLOSE("1", "空调关机"),

    UP("2", "调高温度"),

    DOWN("3", "降低温度"),

    SPEED("4", "风速"),

    MODE("5", "模式"),

    UNKNOW("100", "未知");



    private String type;

    private String name;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    IRVauleEnums(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }
    public static IRVauleEnums fromType(String typeName) {
        for (IRVauleEnums type : IRVauleEnums.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return UNKNOW;
    }

}
