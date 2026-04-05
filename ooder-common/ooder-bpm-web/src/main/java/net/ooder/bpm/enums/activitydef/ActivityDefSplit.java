package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefSplit implements Enumstype {

    DEFAULT("DEFAULT", "默认值"),

    SPLIT_AND("AND", "并行分裂"),

    SPLIT_XOR("XOR", "选择性执行");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefSplit(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefSplit fromType(String typeName) {
	for (ActivityDefSplit type : ActivityDefSplit.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
