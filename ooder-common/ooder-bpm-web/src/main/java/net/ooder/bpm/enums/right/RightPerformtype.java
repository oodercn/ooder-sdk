package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum RightPerformtype implements Enumstype {

    PERSON("PERSON", "人员"),

    ORG("ORG", "ORG");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    RightPerformtype(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RightPerformtype fromType(String typeName) {
	for (RightPerformtype type : RightPerformtype.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
