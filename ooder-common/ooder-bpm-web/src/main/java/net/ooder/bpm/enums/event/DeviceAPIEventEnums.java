package net.ooder.bpm.enums.event;

import net.ooder.annotation.EventEnums;

public enum DeviceAPIEventEnums implements EventEnums {

    register("注册", "register", 9091),

    activate("网关激活", "activate", 9092),

    login("登录", "login", 9093),

    logout("退出", "logout", 9094),

    gatewayOnLine("网关上线", "gatewayOnLine", 9095),

    gatewayOffLine("网关离线", "gatewayOffLine", 9096),

    gatewayErrorReport("网关错误报告上报", "gatewayErrorReport", 9097),

    sensorReport("传感器上报", "sensorReport", 9001),

    dataReport("传感器数据上报", "dataReport", 9002),

    alarmReport("报警信息上报", "alarmReport", 9003),

    sensorOnLine("传感器上线", "sensorOnLine", 9004),

    commandReport("命令报告", "commandReport", 9005),

    findSensor("发现新的传感器", "findSensor", 9006),

    bindListReport("绑定设备列表上报", "bindListReport", 9007),

    bindingStatusReport("绑定执行报告", "bindingStatusReport", 9008),

    modeListReport("模式上报", "modeListReport", 9009),

    sensorOffLine("传感器离线", "sensorOffLine", 9010);

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

    DeviceAPIEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return method.toString();
    }

    public static DeviceAPIEventEnums fromCode(Integer code) {
	for (DeviceAPIEventEnums type : DeviceAPIEventEnums.values()) {
	    if (type.getCode().equals(code)) {
		return type;
	    }
	}
	return null;
    }

    public static DeviceAPIEventEnums fromMethod(String method) {
	for (DeviceAPIEventEnums type : DeviceAPIEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static ActivityEventEnums fromType(String method) {
	for (ActivityEventEnums type : ActivityEventEnums.values()) {
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
