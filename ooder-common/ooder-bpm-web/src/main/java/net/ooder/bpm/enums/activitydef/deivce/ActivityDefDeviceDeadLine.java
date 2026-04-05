package net.ooder.bpm.enums.activitydef.deivce;


import net.ooder.annotation.Enumstype;

public enum ActivityDefDeviceDeadLine implements Enumstype {

    DEFAULT("DEFAULT", "默认值"),

    DELAY("DELAY", "延时执行"),

    TAKEBACK("TAKEBACK", "回退上一节点"),

    GOSTART("GOSTART", "回退到开始节点"),

    SURROGATE("SURROGATE", "人工处理");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }


    ActivityDefDeviceDeadLine(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefDeviceDeadLine fromType(String typeName) {
	for (ActivityDefDeviceDeadLine type : ActivityDefDeviceDeadLine.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
