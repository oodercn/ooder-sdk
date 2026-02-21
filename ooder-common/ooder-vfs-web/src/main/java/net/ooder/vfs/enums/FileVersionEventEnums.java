package net.ooder.vfs.enums;

import  net.ooder.annotation.EventEnums;

public enum FileVersionEventEnums implements EventEnums {

    lockVersion("锁定版本", "lockVersion"),

    addFileVersion("添加版本", "addFileVersion"),

    updateFileVersion("修改版本", "updateFileVersion"),

    deleteFileVersion("删除版本", "deleteFileVersion");

    private String name;

    private String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    FileVersionEventEnums(String name, String method) {
        this.name = name;
        this.method = method;
    }

    @Override
    public String toString() {
        return method.toString();
    }


    public static FileVersionEventEnums fromMethod(String method) {
        for (FileVersionEventEnums type : FileVersionEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    public static FileVersionEventEnums fromType(String method) {
        for (FileVersionEventEnums type : FileVersionEventEnums.values()) {
            if (type.getMethod().equals(method)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        return method.toString();
    }

}
