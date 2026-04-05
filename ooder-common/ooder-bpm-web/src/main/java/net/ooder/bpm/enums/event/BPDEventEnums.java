package net.ooder.bpm.enums.event;

import net.ooder.annotation.EventEnums;

public enum BPDEventEnums implements EventEnums {


    PROCESSDEFCREATED("创建流程完毕", "ProcessDefCreadED", 9006),

    PROCESSDEFDELETED("流程删除完毕", "ProcessDefDeleted", 9007),

    PROCESSDEFUPDATE("流程更新", "ProcessDefUpdate", 9008),

    PROCESSDEFFREEZED("冻结流程完毕", "ProcessDefFreezed", 9009),

    PROCESSDEFACTIVATED("激活流程完毕", "ProcessDefActivaed", 9010);

    private String name;

    private Integer code;

    private String method;

    public Integer getCode() {
        return code;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    BPDEventEnums(String name, String method, Integer code) {

        this.name = name;
        this.method = method;
        this.code = code;

    }

    @Override
    public String toString() {
        return method.toString();
    }

    public static BPDEventEnums fromCode(Integer code) {
        for (BPDEventEnums type : BPDEventEnums.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static BPDEventEnums fromMethod(String method) {
        for (BPDEventEnums type : BPDEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static BPDEventEnums fromType(String method) {
        for (BPDEventEnums type : BPDEventEnums.values()) {
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
