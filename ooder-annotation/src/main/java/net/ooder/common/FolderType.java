package net.ooder.common;


import net.ooder.annotation.Enums;

public enum FolderType implements Enums {

    folder("folder", "普通文件夹"),

    disk("disk", "虚拟盘"),

    cdisk("cdisk", "企业盘"),

    pdisk("pdisk", "个人盘"),

    ref("ref", "引用"),

    space("space", "工作空间"),

    project("project", "OOD工程"),

    systemspace("systemspace", "系统空间"),;

    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    FolderType(String type, String name) {
        this.type = type;
        this.name = name;

    }

    @Override
    public String toString() {
        return type;
    }

    public static FolderType fromType(String typeName) {
        for (FolderType type : FolderType.values()) {
            if (type.getType().equals(typeName)) {
                return type;
            }
        }
        return folder;
    }

}
