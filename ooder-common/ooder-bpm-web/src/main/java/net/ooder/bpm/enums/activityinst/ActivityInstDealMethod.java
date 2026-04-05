package net.ooder.bpm.enums.activityinst;


import net.ooder.annotation.Enumstype;

public enum ActivityInstDealMethod implements Enumstype {

    DEALMETHOD_NORMAL("NORMAL", "正常"),

    DEALMETHOD_INSTEAD("INSTEAD", "代办"),

    DEFAULT("DEFAULT", "默认值"),

    DEALMETHOD_SPLITED("SPLITED", "会签");

    ActivityInstDealMethod(String type, String name) {
	this.type = type;
	this.name = name;

    }

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityInstDealMethod fromType(String typeName) {
	for (ActivityInstDealMethod type : ActivityInstDealMethod.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return DEFAULT;
    }

}
