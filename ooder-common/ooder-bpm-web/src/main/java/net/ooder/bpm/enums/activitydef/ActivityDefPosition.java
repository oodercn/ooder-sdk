package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefPosition implements Enumstype {
    POSITION_NORMAL("NORMAL", "普通"),
    
    POSITION_START("START", "起始活动 "),
    
    POSITION_END("END", "结束活动 "),

    VIRTUAL_LAST_DEF("LAST", "LAST");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefPosition(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefPosition fromType(String typeName) {
	for (ActivityDefPosition type : ActivityDefPosition.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return POSITION_NORMAL;
    }

}
