package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.EventEnums;

public enum SensorEventEnums implements EventEnums {

    addDesktop("添加到桌面", "addDesktop"),

    removeDesktop("移除桌面", "removeDesktop"),

    addAlarm("添加到报警列表", "addDesktop"),

    removeAlarm("移除报警列表", "removeDesktop"),

    sceneAdded("添加场景", "sceneAdded"),

    sceneRemoved("移除场景", "sceneRemoved"),

    close("关闭", "close"),

    start("打开", "start");


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

    SensorEventEnums(String name, String method) {

	this.name = name;
	this.method = method;


    }

    @Override
    public String toString() {
	return method.toString();
    }


    public static SensorEventEnums fromMethod(String method) {
	for (SensorEventEnums type : SensorEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static SensorEventEnums fromType(String method) {
	for (SensorEventEnums type : SensorEventEnums.values()) {
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
