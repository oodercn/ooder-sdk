package net.ooder.bpm.enums.process;


import net.ooder.annotation.Enumstype;

public enum ProcessDefVersionStatus implements Enumstype {

    UNDER_REVISION("UNDER_REVISION", "冻结"),

    RELEASED("RELEASED", "激活"),

    UNDER_TEST("UNDER_TEST", "测试中");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ProcessDefVersionStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ProcessDefVersionStatus fromType(String typeName) {
	for (ProcessDefVersionStatus type : ProcessDefVersionStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
