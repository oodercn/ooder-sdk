package net.ooder.bpm.enums.activitydef.deivce;


import net.ooder.annotation.Enumstype;

public enum ActivityDefDeviceSpecial implements Enumstype {

    ALL("ALL", "任意命令"),

    PERFORMERS("PERFORMERS", "曾经发送的命令"),

    DEFAULT("DEFAULT", "默认值");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefDeviceSpecial(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefDeviceSpecial fromType(String typeName) {
	for (ActivityDefDeviceSpecial type : ActivityDefDeviceSpecial.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
