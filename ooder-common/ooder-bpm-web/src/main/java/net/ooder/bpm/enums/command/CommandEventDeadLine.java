package net.ooder.bpm.enums.command;


import net.ooder.annotation.Enumstype;

public enum CommandEventDeadLine implements Enumstype {

    TAKEBACK("TAKEBACK", "退回重新发送"),
    
    TAKEBACKSTART("TAKEBACKSTART", "退回起始节点"),

    THROWS("THROWS", "抛出异常"),

    STOP("STOP", "终止监听"),

    CONTINUE("CONTINUE", "强制推进"),

    DEFAULT("DEFAULT", "默认值");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    CommandEventDeadLine(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static CommandEventDeadLine fromType(String typeName) {
	for (CommandEventDeadLine type : CommandEventDeadLine.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
