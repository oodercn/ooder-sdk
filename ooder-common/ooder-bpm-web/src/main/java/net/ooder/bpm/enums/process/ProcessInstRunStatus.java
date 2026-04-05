package net.ooder.bpm.enums.process;


import net.ooder.annotation.Enumstype;

public enum ProcessInstRunStatus implements Enumstype {

    NORMAL("NORMAL", "正常"),

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

    ProcessInstRunStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ProcessInstRunStatus fromType(String typeName) {
	for (ProcessInstRunStatus type : ProcessInstRunStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return NORMAL;
    }
}
