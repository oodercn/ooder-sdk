package net.ooder.bpm.enums.activitydef.service;


import net.ooder.annotation.Enumstype;

public enum ActivityDefServicePerformtype implements Enumstype {

    NOSELECT("NOSELECT", "不需要选择直接执行"),

    SINGLE("SINGLE", "单选设备（只能选一个设备）"),

    MULTIPLE("MULTIPLE", "多设备同时执行（用户选择）"),

    JOINTSIGN("JOINTSIGN", "动态选择设备"),

    DEFAULT("DEFAULT", "默认值");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefServicePerformtype(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefServicePerformtype fromType(String typeName) {
	for (ActivityDefServicePerformtype type : ActivityDefServicePerformtype.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
