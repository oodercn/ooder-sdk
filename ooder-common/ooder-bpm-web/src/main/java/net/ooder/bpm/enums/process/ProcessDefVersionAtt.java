package net.ooder.bpm.enums.process;


import net.ooder.annotation.Enumstype;

public enum ProcessDefVersionAtt implements Enumstype {

    MOVEPERFORMERTO("MOVEPERFORMERTO", "办理后权限");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    ProcessDefVersionAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ProcessDefVersionAtt fromType(String typeName) {
	for (ProcessDefVersionAtt type : ProcessDefVersionAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return MOVEPERFORMERTO;
    }

}
