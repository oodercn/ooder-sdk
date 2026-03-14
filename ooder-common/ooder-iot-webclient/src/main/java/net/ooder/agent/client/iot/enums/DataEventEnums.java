package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.EventEnums;

public enum DataEventEnums implements EventEnums {
    
    AttributeReport("数据上报", "AttributeReport", 8001),

    DataReport("数据上报", "DataReport", 8002),

    AlarmReport("报警信息", "AlarmReport", 8003);

    private String name;

    private Integer code;

    private String method;

    public Integer getCode() {
	return code;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setCode(Integer code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    DataEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return method.toString();
    }

    public static DataEventEnums fromCode(Integer code) {
	for (DataEventEnums type : DataEventEnums.values()) {
	    if (type.getCode().equals(code)) {
		return type;
	    }
	}
	return null;
    }

    public static DataEventEnums fromMethod(String method) {
	for (DataEventEnums type : DataEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static DataEventEnums fromType(String method) {
	for (DataEventEnums type : DataEventEnums.values()) {
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
