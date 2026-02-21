package net.ooder.common;


import net.ooder.annotation.Enumstype;

public enum SystemType implements Enumstype {


    dev("dev", "开发服务"),

    system("system", "系统服务"),

    userdef("userdef", "应用服务"),

    app("app", "业务应用"),

    factory("factory", "企业工厂");

    private String type;


    private String name;


    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }


    SystemType(String type, String name) {
        this.type = type;
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

    public static SystemType fromType(String typeName) {
        if (typeName!=null){
            for (SystemType type : SystemType.values()) {
                if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                    return type;
                }
            }
        }

        return dev;
    }


}
