package net.ooder.common;

import net.ooder.annotation.Enumstype;

public enum SystemNodeType implements Enumstype {


    MAIN("MAIN", "主节点"),

    SUB("SUB", "子节点"),

    CLUSTER("CLUSTER", "调度节点"),

    BACK("BACK", "备份节点");

    private String type;


    private String name;


    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }


    SystemNodeType(String type, String name) {
        this.type = type;
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

    public static SystemNodeType fromType(String typeName) {
        for (SystemNodeType type : SystemNodeType.values()) {
            if (type.getType().toUpperCase().equals(typeName.toUpperCase())) {
                return type;
            }
        }
        return SUB;
    }


}
