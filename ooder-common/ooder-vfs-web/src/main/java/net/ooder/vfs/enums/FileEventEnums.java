package net.ooder.vfs.enums;

import  net.ooder.annotation.EventEnums;

public enum FileEventEnums implements EventEnums {

    beforeUpLoad("开始上传文件", "beforeUpLoad"),

    upLoading("上传中", "upLoading"),

    upLoadEnd("上传完毕", "upLoadEnd"),

    upLoadError("上传错误", "upLoadError"),


    create("文件创建", "create"),

    beforeCopy("文件开始复制", "beforeCopy"),

    copyEnd("文件复制完成", "copyEnd"),

    save("文件保存", "save"),

    beforeUpdate("文件开始开始", "beforeUpdate"),

    updateEnd("文件更新完成", "updateEnd"),

    beforeMove("文件开始移动", "beforeMove"),

    moveEnd("文件移动", "moveEnd"),

    beforeDelete("文件开始删除", "beforeDelete"),

    deleteEnd("文件删除完毕", "deleteEnd"),

    send("文件发送", "send"),

    open("文件打开", "open"),

    clear("彻底清除", "clear"),

    share("文件分享", "share"),

    beforeDownLoad("文件下载", "beforeDownLoad"),

    downLoading("文件下载中", "downLoading"),

    downLoadEnd("文件下载结束", "downLoadEnd"),

    beforeReName("开始修改名称", "beforeReName"),

    reNameEnd("修改名称结束", "reNameEnd"),

    reStore("还原", "reStore");

    private String name;

    private Integer code;

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

    FileEventEnums(String name, String method) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return method.toString();
    }



    public static FileEventEnums fromMethod(String method) {
	for (FileEventEnums type : FileEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static FileEventEnums fromType(String method) {
	for (FileEventEnums type : FileEventEnums.values()) {
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
