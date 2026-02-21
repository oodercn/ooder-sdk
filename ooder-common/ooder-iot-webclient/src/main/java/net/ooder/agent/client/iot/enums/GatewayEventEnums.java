package net.ooder.agent.client.iot.enums;

import  net.ooder.annotation.EventEnums;

public enum GatewayEventEnums implements EventEnums {
    GATEWAYONLINE("网关上线", "gatewayOnLine", 2001),

    GATEWAYOFFINE("网关离线", "gatewayOffLine", 2002),

    SENSORADDING("添加传感器", "sensorAdding", 2003),

    SENSORADDED("添加成功", "sensorAdded", 2004),

    SENSORREMOVING("移除传感器", "sensorRemoving", 2005),

    SENSORREMOVED("移除成功", "sensorRemoved", 2006),
    
    GATEWAYSHARING("开始共享网关", "gatewaySharing", 2007),

    GATEWAYSHARED("共享设置完成", "gatewayShared", 2025),

    GATEWAYLOCKED("网关锁定", "gatewayLocked", 2009),

    GATEWAYUNLOCKED("解锁成功", "gatewayUnLocked", 2010),
    

    ACCOUNTBIND("绑定账号", "accountBind", 2011),

    ACCOUNTUNBIND("解绑账号", "accountUNBind", 2012);
    
    
 

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

    GatewayEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return method.toString();
    }

    public static GatewayEventEnums fromCode(Integer code) {
	for (GatewayEventEnums type : GatewayEventEnums.values()) {
	    if (type.getCode().equals(code)) {
		return type;
	    }
	}
	return null;
    }

    public static GatewayEventEnums fromMethod(String method) {
	for (GatewayEventEnums type : GatewayEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static GatewayEventEnums fromType(String method) {
	for (GatewayEventEnums type : GatewayEventEnums.values()) {
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
