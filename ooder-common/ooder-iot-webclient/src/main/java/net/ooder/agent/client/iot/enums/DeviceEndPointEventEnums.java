package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.EventEnums;

public enum DeviceEndPointEventEnums implements EventEnums {


    bind("开始绑定", "bind"),

    updateInfo("更新EP信息", "updateInfo"),

    bindSuccess("绑定成功", "bindSuccess"),

    bindFail("绑定失败", "bindFail"),

    unbind("开始解绑", "unbind"),

    unbindSuccess("解绑成功", "unbindSuccess"),

    unbindFail("解绑失败", "unbindFail"),

    createEndPoint("添加节点", "createEndPoint"),

    removeEndPoint("移除节点", "removeEndPoint"),

    locked("锁定", "locked"),

    unLocked("解锁", "unLocked"),

    alarmAdd("报警完毕", "alarmAdd");


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

    DeviceEndPointEventEnums(String name, String method) {

	this.name = name;
	this.method = method;


    }

    @Override
    public String toString() {
	return method.toString();
    }


    public static DeviceEndPointEventEnums fromMethod(String method) {
	for (DeviceEndPointEventEnums type : DeviceEndPointEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static DeviceEndPointEventEnums fromType(String method) {
	for (DeviceEndPointEventEnums type : DeviceEndPointEventEnums.values()) {
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
