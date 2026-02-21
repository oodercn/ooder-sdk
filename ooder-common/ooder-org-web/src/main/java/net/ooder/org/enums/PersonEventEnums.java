package net.ooder.org.enums;

import net.ooder.annotation.EventEnums;

public enum PersonEventEnums implements EventEnums {


    CREATE("创建", "CREATE", 2006),

    UPDATE("更新", "UPDATE", 2009),

    DELETED("删除", "DELETED", 2009);



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

    PersonEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return method.toString();
    }

    public static PersonEventEnums fromCode(Integer code) {
	for (PersonEventEnums type : PersonEventEnums.values()) {
	    if (type.getCode().equals(code)) {
		return type;
	    }
	}
	return null;
    }

    public static PersonEventEnums fromMethod(String method) {
	for (PersonEventEnums type : PersonEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static PersonEventEnums fromType(String method) {
	for (PersonEventEnums type : PersonEventEnums.values()) {
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
