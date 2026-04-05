package net.ooder.bpm.enums.activitydef.task;


import net.ooder.annotation.Enumstype;

public enum DelayTime implements Enumstype {

    none("30", "30秒"),

    one("60", "60秒"),

    two("5M", "5分钟"),

    three("15", "3次"),

    five("5", "5次"),

    ten("10", "10次"),

    forever("-1", "不限次数");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    
    public String getName() {
	return name;
    }

    DelayTime(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static DelayTime fromType(String typeName) {
	for (DelayTime type : DelayTime.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
