package net.ooder.bpm.enums.event;


import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.common.CommandEventEnums;

public enum ListenerTypeEnums implements AttributeName {

    CommandEvent("CommandEvent", "命令事件", CommandEventEnums.class),

    ListenerEvent("ListenerEvent", "事件类型", ListenerEnums.class),

    ExpressionListenerType("ExpressionListenerType", "监听器类型", ExpressionTypeEnums.class),

    ExpressionEventType("ExpressionEventType", "表达式类型", ActivityEventEnums.class);

    public Class<? extends Enumstype> getClazz() {
	return clazz;
    }

    public void setClazz(Class<? extends Enumstype> clazz) {
	this.clazz = clazz;
    }

    private String type;
    private Class<? extends Enumstype> clazz;
    private String name;
    private String displayName;

    ListenerTypeEnums(String type, String name, Class<? extends Enumstype> clazz) {

	this.type = type;
	this.displayName=name;
	this.name = name();
	this.clazz = clazz;

    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String getType() {
	return type;
    }

    public static ListenerTypeEnums fromType(String typeName) {
	for (ListenerTypeEnums type : ListenerTypeEnums.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

    @Override
    public String getDisplayName() {
	return displayName;
    }

}
