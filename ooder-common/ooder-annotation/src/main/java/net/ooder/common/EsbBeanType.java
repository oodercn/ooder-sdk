package net.ooder.common;


import net.ooder.annotation.Enumstype;

public enum EsbBeanType implements Enumstype {

    System("系统服务", "System"),

    Cluster("集群服务", "Cluster"),

    Remote("远程调用", "Remote"),

    Local("本地服务", "Local");

    private String name;

    private String type;


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    EsbBeanType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }


    public static EsbBeanType fromType(String type) {
        for (EsbBeanType contextType : EsbBeanType.values()) {
            if (contextType.getType().toUpperCase().equals(type.toUpperCase())) {
                return contextType;
            }
        }
        return Local;
    }

    @Override
    public String getType() {
        return type.toString();
    }


}
