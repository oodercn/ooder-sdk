package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefJoin implements Enumstype {
    
    DEFAULT("DEFAULT", "默认值"), 
    
    JOIN_AND("AND", "等待合并"),
    
    JOIN_XOR("XOR", "不等待异步推进");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefJoin(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefJoin fromType(String typeName) {
	for (ActivityDefJoin type : ActivityDefJoin.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
