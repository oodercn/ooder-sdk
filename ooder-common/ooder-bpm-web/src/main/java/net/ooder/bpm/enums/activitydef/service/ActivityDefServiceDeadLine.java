package net.ooder.bpm.enums.activitydef.service;


import net.ooder.annotation.Enumstype;

public enum ActivityDefServiceDeadLine implements Enumstype {

    DELAY("DELAY", "延时发送"),

    TAKEBACK("TAKEBACK", "取消发送"),
    
    DEFAULT("DEFAULT", "默认值");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    ActivityDefServiceDeadLine(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefServiceDeadLine fromType(String typeName) {
	for (ActivityDefServiceDeadLine type : ActivityDefServiceDeadLine.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
