package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum RightPermission implements Enumstype {

    PERMISSION_PERFORM("PERMISSION_PERFORM", "写权限"),

    PERMISSION_READ("PERMISSION_READ", "读权限"),

    PERMISSION_ANY("PERMISSION_ANY", "任意权限");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    RightPermission(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RightPermission fromType(String typeName) {
	for (RightPermission type : RightPermission.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return PERMISSION_ANY;
    }

}
