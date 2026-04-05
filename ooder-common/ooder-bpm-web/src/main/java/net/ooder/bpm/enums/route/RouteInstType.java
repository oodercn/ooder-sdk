package net.ooder.bpm.enums.route;


import net.ooder.annotation.Enumstype;

public enum RouteInstType implements Enumstype {

    HISTORY("HISTORY", "历史"),

    ACTIVITY("ACTIVITY", "实例");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    RouteInstType(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RouteInstType fromType(String typeName) {
	for (RouteInstType type : RouteInstType.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return ACTIVITY;
    }

}
