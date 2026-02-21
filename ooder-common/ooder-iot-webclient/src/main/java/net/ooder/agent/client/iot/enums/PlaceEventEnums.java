package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.EventEnums;

public enum PlaceEventEnums implements EventEnums {

    placeCreate("添加成功", "placeCreate"),

    placeRemove("移除成功", "placeRemove("),

    areaAdd("添加房间", "areaAdded"),

    areaRemove("移除房间", "areaRemove"),

    gatewayAdd("网关添加", "gatewayAdd"),

    gatewayRemove("移除网关", "gatewayRemove");


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

    PlaceEventEnums(String name, String method) {

	this.name = name;
	this.method = method;


    }

    @Override
    public String toString() {
	return method.toString();
    }



    public static PlaceEventEnums fromMethod(String method) {
	for (PlaceEventEnums type : PlaceEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static PlaceEventEnums fromType(String method) {
	for (PlaceEventEnums type : PlaceEventEnums.values()) {
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
