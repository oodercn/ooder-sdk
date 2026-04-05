package net.ooder.bpm.enums.activitydef.event;


import net.ooder.annotation.Enumstype;

public enum ActivityDefEventSpecial implements Enumstype {

    ALL("ALL", "任意命令"),

    DEFAULT("DEFAULT", "默认值"),

    PERFORMERS("PERFORMERS", "曾经发送的命令");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefEventSpecial(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefEventSpecial fromType(String typeName) {
	for (ActivityDefEventSpecial type : ActivityDefEventSpecial.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
