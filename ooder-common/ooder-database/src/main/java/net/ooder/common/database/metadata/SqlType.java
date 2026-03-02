/**
 * $RCSfile: SqlType.java,v $
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
package net.ooder.common.database.metadata;

import net.ooder.common.database.ProfiledConnection;


public enum SqlType {

    SELECT("SELECT", ProfiledConnection.SELECT, "查询"),

    UPDATE("UPDATE", ProfiledConnection.UPDATE, "更新"),

    INSERT("INSERT", ProfiledConnection.INSERT, "插入"),

    DELETE("DELETE", ProfiledConnection.DELETE, "删除");


    private Integer type;

    private String name;

    private String desc;


    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    SqlType(String name, Integer type, String desc) {
        this.type = type;
        this.name = name;
        this.desc = desc;


    }

    @Override
    public String toString() {
        return name;
    }

    public static SqlType fromType(String typeName) {
        for (SqlType type : SqlType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    public static SqlType fromName(String name) {
        for (SqlType type : SqlType.values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}


