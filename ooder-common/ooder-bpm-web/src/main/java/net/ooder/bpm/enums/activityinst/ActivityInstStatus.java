package net.ooder.bpm.enums.activityinst;


import net.ooder.annotation.Enumstype;

public enum ActivityInstStatus implements Enumstype {

    ENDREAD("ENDREAD", "阅闭"),

    READ("READ", "阅办"),

    running("running", "正常"),

    open("open", "打开"),

    notRunning("notRunning", "报警"),

    notStarted("notStarted", "未启动"),

    suspended("suspended", "suspended"),

    closed("closed", "closed"),

    aborted("aborted", "aborted"),

    terminated("terminated", "terminated"),

    completed("completed", "completed");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityInstStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityInstStatus fromType(String typeName) {
	for (ActivityInstStatus type : ActivityInstStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return running;
    }

}
