package net.ooder.bpm.enums.activitydef.deivce;


import net.ooder.annotation.Enumstype;

public enum ActivityDefDevicePerformtype implements Enumstype {

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


    ActivityDefDevicePerformtype(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefDevicePerformtype fromType(String typeName) {
	for (ActivityDefDevicePerformtype type : ActivityDefDevicePerformtype.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
