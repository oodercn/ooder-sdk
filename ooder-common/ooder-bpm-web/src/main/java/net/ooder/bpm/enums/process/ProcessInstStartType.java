package net.ooder.bpm.enums.process;


import net.ooder.annotation.Enumstype;

public enum ProcessInstStartType implements Enumstype {

    AUTO("AUTO", "自动启动"),

    MANUAL("MANUAL", "手动启动");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ProcessInstStartType(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ProcessInstStartType fromType(String typeName) {
	for (ProcessInstStartType type : ProcessInstStartType.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return MANUAL;
    }

}
