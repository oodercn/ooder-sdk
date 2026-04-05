package net.ooder.bpm.enums.event;

import net.ooder.annotation.EventEnums;

public enum RightEventEnums implements EventEnums {

    ROUTETO("发送", "routeTo", 6001),

    COPYTO("抄送", "copyTo", 6002),

    SIGNRECEIVE("开始签收", "signReceive", 6003),

    ENDREAD("阅毕操作", "endRead", 6004),

    ROUTBACK("退回操作", "routeBack", 6005),

    TACKBACK("办理人收回", "tackBack", 6006),

    CHANGEPERFORMER("更换办理人", "changePerformer", 6006);

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

    RightEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return code.toString();
    }

    public static RightEventEnums fromMethod(String method) {
	for (RightEventEnums type : RightEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static RightEventEnums fromCode(Integer code) {
	for (RightEventEnums type : RightEventEnums.values()) {
	    if (type.getCode().equals(code)) {
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
