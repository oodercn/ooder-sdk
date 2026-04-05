package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum RightPerformStatus implements Enumstype {

    WAITING("WAITING", "等待办理"),

    CURRENT("CURRENT", "正在办理"),

    FINISH("FINISH", "办理完成"),

    READ("READ", "正在阅办"),

    ENDREAD("ENDREAD", "阅闭"),

    DELETE("DELETE", "删除"),

    NULL("", "等待办理"),

    CLEAR("CLEAR", "清空");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    RightPerformStatus(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RightPerformStatus fromType(String typeName) {
	for (RightPerformStatus type : RightPerformStatus.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return NULL;
    }

}
