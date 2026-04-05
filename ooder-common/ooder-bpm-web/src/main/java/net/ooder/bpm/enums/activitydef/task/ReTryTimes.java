package net.ooder.bpm.enums.activitydef.task;


import net.ooder.annotation.Enumstype;

public enum ReTryTimes implements Enumstype {

    none("0", "不循环"),

    one("1", "1次"),

    two("2", " 2次"),

    three("3", "3次"),

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

    ReTryTimes(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ReTryTimes fromType(String typeName) {
	for (ReTryTimes type : ReTryTimes.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
