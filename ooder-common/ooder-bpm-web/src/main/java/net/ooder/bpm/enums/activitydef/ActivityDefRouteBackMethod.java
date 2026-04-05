package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefRouteBackMethod implements Enumstype {

    DEFAULT("DEFAULT", "默认值"),

    LAST("LAST", "上一活动"),

    ANY("ANY", "退回经过的任意活动"),

    SPECIFY("SPECIFY", "条件退回");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefRouteBackMethod(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefRouteBackMethod fromType(String typeName) {
	for (ActivityDefRouteBackMethod type : ActivityDefRouteBackMethod.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
