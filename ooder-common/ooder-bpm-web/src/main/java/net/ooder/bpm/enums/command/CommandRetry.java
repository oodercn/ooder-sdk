package net.ooder.bpm.enums.command;


import net.ooder.annotation.Enumstype;

public enum CommandRetry implements Enumstype {

    WRTE("WRTE", "等待"),

    GOON("GOON", "循环执行"),

    STOP("STOP", "停止");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    CommandRetry(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static CommandRetry fromType(String typeName) {
	for (CommandRetry type : CommandRetry.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
