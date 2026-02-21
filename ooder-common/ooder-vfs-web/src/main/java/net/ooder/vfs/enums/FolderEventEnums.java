package net.ooder.vfs.enums;

import  net.ooder.annotation.EventEnums;

public enum FolderEventEnums implements EventEnums {

    create("创建文件", "create"),

    lock("文件夹锁定", "lock"),

    beforeReName("名称开始修改", "beforeReName"),

    reNameEnd("名称开始修改", "reNameEnd"),

    beforeDelete("开始删除", "beforeDelete"),

    deleteing("删除进行中", "deleteing"),

    deleteEnd("删除完成", "deleteEnd"),

    save("保存完成", "save"),

    beforeCopy("Copy开始", "beforeCopy"),

    copying("正在COPY", "copying"),

    copyEnd("Copy完成", "copyEnd"),

    beforeMove("Move开始", "beforeMove"),

    moving("正在Move", "moving"),

    moveEnd("Move完成", "moveEnd"),

    beforeClean("开始清空", "beforeClean"),

    cleanEnd("清空完成", "cleanEnd"),

    reStore("还原完成", "reStore");

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

    FolderEventEnums(String name, String method) {

	this.name = name;
	this.method = method;


    }

    @Override
    public String toString() {
	return method.toString();
    }


    public static FolderEventEnums fromMethod(String method) {
	for (FolderEventEnums type : FolderEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static FolderEventEnums fromType(String method) {
	for (FolderEventEnums type : FolderEventEnums.values()) {
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
