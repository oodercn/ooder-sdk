package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum CommissionEnums implements Enumstype {

    READER("READER", "读者"),

    WRITER("WRITER", "执行者"),

    OWNER("OWNER", "所有者");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    
    public String getName() {
	return name;
    }

    CommissionEnums(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static CommissionEnums fromType(String typeName) {
	for (CommissionEnums type : CommissionEnums.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

    public static CommissionEnums fromName(String name) {
	for (CommissionEnums type : CommissionEnums.values()) {
	    if (type.getName().equals(name)) {
		return type;
	    }
	}
	return null;
    }

}
