/**
 * $RCSfile: DeadLine.java,v $
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

/**
 * 超时处理办法
 * @author wenzhang
 *
 */
public  enum DeadLine implements Enumstype {


    DELAY("DELAY", "延时等待"),

    GOON("GOON", "忽略继续"),

    STOP("STOP", "终止执行");

    private String type;

    private String name;

    public String getType() {
        return type;
    }


    public String getName() {
        return name;
    }

    DeadLine(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static DeadLine fromType(String typeName) {
        for (DeadLine type : DeadLine.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

}
