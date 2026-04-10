package net.ooder.common;

import net.ooder.annotation.IconEnumstype;

public enum EngineType implements IconEnumstype {
    CUSTOM("ri-settings-3-line", "通用操作"),
    ESD("ri-upload-line", "内部应用"),
    IOT("ri-cpu-line", "IOT引擎"),
    ESB("ri-server-line", "服务调度引擎");
    
    private String type;

    private String name;

    private String imageClass;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    @Override
    public String getImageClass() {
        return imageClass;
    }

    EngineType(String imageClass, String name) {
        this.imageClass = imageClass;
        this.type = name();
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static EngineType fromType(String typeName) {
        for (EngineType type : EngineType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return CUSTOM;
    }

}
