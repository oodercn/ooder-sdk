package net.ooder.bpm.enums.activitydef.event;


import net.ooder.annotation.Enumstype;

public enum ActivityDefEventDeadLine implements Enumstype {

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


    ActivityDefEventDeadLine(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefEventDeadLine fromType(String typeName) {
	for (ActivityDefEventDeadLine type : ActivityDefEventDeadLine.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
