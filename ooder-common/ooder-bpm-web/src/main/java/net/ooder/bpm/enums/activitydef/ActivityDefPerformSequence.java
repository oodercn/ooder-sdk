package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefPerformSequence implements Enumstype {

    FIRST("FIRST", "抢占"),

    SEQUENCE("SEQUENCE", "顺序办理"),

    MEANWHILE("MEANWHILE", "同时办理"),

    AUTOSIGN("AUTOSIGN", "自动签收"),

    DEFAULT("DEFAULT", "默认值");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefPerformSequence(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefPerformSequence fromType(String typeName) {
	for (ActivityDefPerformSequence type : ActivityDefPerformSequence.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
