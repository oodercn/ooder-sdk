/**
 * $RCSfile: PerformSequence.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.cluster.udp;


import net.ooder.annotation.Enumstype;

public enum PerformSequence implements Enumstype {

    FIRST("FIRST", "单例抢占执行"),

    SEQUENCE("SEQUENCE", "顺序执行（执行排队）"),

    MEANWHILE("MEANWHILE", "并行执行办理"),

    DEFAULT("DEFAULT", "默认值");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    PerformSequence(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static PerformSequence fromType(String typeName) {
        for (PerformSequence type : PerformSequence.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return DEFAULT;
    }

}
