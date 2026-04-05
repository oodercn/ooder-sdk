package net.ooder.bpm.enums.activityinst;


import net.ooder.annotation.Enumstype;

public enum ActivityInstSuSpend implements Enumstype {

    SUSPEND("SUSPEND", "分裂"),

    COMBINE("COMBINE", "合并"),

    NORMAL("NORMAL", "正常");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityInstSuSpend(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityInstSuSpend fromType(String typeName) {
	for (ActivityInstSuSpend type : ActivityInstSuSpend.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return NORMAL;
    }
}
