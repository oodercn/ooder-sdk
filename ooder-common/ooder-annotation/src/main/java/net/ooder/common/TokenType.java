package net.ooder.common;

import net.ooder.annotation.Enumstype;

public enum TokenType implements Enumstype {

    user("普通用户（需登录）", "user"),

    admin("管理用户（需登录）", "admin"),

    guest("访客（无需登录）", "guest");

    private String name;

    private String type;


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    TokenType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return type.toString();
    }


    public static TokenType fromType(String type) {
        if (type != null) {
            for (TokenType contextType : TokenType.values()) {
                if (contextType.getType().toUpperCase().equals(type.toUpperCase())) {
                    return contextType;
                }
            }
        }
        return user;
    }

    @Override
    public String getType() {
        return type.toString();
    }


}
