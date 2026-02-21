package net.ooder.common;


import net.ooder.annotation.Enums;

public enum FolderState implements Enums {

    locked("lock", "锁定"),

    tested("tested", "测试"),

    deleted("deleted", "已删除"),

    normal("normal", "正常");

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    FolderState(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static FolderState fromType(String typeName) {
        for (FolderState type : FolderState.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return tested;
    }

}
