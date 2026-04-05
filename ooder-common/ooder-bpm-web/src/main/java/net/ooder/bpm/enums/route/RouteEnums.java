package net.ooder.bpm.enums.route;


import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;

public enum RouteEnums implements AttributeName {
    RouteDirction("Type", "路由方向", net.ooder.bpm.enums.route.RouteDirction.class),

    RouteCondition("Type", "执行条件", net.ooder.bpm.enums.route.RouteCondition.class);

    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    RouteEnums(String name, String displayName, Class<? extends Enumstype> clazz) {

	this.name = name;
	this.displayName = displayName;
	this.clazz = clazz;

    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Class<? extends Enumstype> getClazz() {
	return clazz;
    }

    public void setClazz(Class<? extends Enumstype> clazz) {
	this.clazz = clazz;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    @Override
    public String getType() {
	return name;
    }

}
