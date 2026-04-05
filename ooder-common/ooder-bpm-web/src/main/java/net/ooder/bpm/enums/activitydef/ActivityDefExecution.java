package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefExecution implements Enumstype {

    ASYNCHR("ASYNCHR", "异步"), DEFAULT("SYNCHR", "同步");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefExecution(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefExecution fromType(String typeName) {
	for (ActivityDefExecution type : ActivityDefExecution.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return ASYNCHR;
    }

}
