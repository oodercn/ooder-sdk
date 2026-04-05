package net.ooder.bpm.enums.route;


import net.ooder.annotation.Enumstype;

public enum RouteDirction implements Enumstype {

    FORWARD("FORWARD", "前进路由"),

    BACK("BACKWARD", "退回路由"),

    SPECIAL("SPECIAL", "特送 ");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    RouteDirction(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RouteDirction fromType(String typeName) {
	for (RouteDirction type : RouteDirction.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return FORWARD;
    }

}
