package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefSpecialSendScope implements Enumstype {

    ALL("ALL", "所有人"),

    DEFAULT("DEFAULT", "默认值"),

    PERFORMERS("PERFORMERS", "曾经的办理人");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefSpecialSendScope(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefSpecialSendScope fromType(String typeName) {
	for (ActivityDefSpecialSendScope type : ActivityDefSpecialSendScope.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
