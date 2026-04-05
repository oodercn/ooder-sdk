package net.ooder.bpm.enums.command;


import net.ooder.annotation.Enumstype;

public enum CommandExecType implements Enumstype {

    MULTIPLENOWITE("MULTIPLENOWITE", "并行执行(不等待)"),
    
    MULTIPLEWITE("MULTIPLEWITE", "并行执行(等待)"),

    SIGNWRITE("MULTIPLE", "串行执行设备(等待)"),

    SIGN("SIGN", "选择性执行"),

    SIGN_TIMEOUT("TIMEOUT", "超时等待");
    ;

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    CommandExecType(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static CommandExecType fromType(String typeName) {
	for (CommandExecType type : CommandExecType.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
