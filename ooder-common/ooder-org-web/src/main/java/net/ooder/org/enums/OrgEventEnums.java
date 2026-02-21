package net.ooder.org.enums;

import net.ooder.annotation.EventEnums;

public enum OrgEventEnums implements EventEnums {

    CREATE("创建", "CREATE", 1000),

    ADDPERSON("添加人员", "ADDPERSON", 1001),

    REMOVEERSON("添加人员", "ADDPERSON", 1002),

    ADDCHILD("添加部门", "ADDCHILD", 1003),

    UPDATE("修改完成", "UPDATE", 1005),

    DELET("删除完成", "DELET", 1007);



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

    OrgEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return method.toString();
    }

    public static OrgEventEnums fromCode(Integer code) {
	for (OrgEventEnums type : OrgEventEnums.values()) {
	    if (type.getCode().equals(code)) {
		return type;
	    }
	}
	return null;
    }

    public static OrgEventEnums fromMethod(String method) {
	for (OrgEventEnums type : OrgEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static OrgEventEnums fromType(String method) {
	for (OrgEventEnums type : OrgEventEnums.values()) {
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
