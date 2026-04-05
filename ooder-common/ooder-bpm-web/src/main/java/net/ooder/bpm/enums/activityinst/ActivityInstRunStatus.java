package net.ooder.bpm.enums.activityinst;


import net.ooder.annotation.Enumstype;

public enum ActivityInstRunStatus implements Enumstype {

    NORMAL("NORMAL", "正常"),

    PROCESSNOTSTARTED("PROCESSNOTSTARTED", "未启动"),

    DELAY("DELAY", "延期"),

    URGENCY("URGENCY", "催办"),

   ALERT("ALERT", "报警");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityInstRunStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityInstRunStatus fromType(String typeName) {
	for (ActivityInstRunStatus type : ActivityInstRunStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return NORMAL;
    }
}
