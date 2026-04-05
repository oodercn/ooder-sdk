package net.ooder.bpm.enums.command;


import net.ooder.annotation.Enumstype;

public enum CommandConditionEnums implements Enumstype {

    CONDITION_WAITEDCOMMAND("CONDITION_WAITEDCOMMAND", "等待执行的命令"),

    CONDITION_CURRENTCOMMAND("CONDITION_CURRENTCOMMAND", "正在执行的命令"),

    CONDITION_JOINCOMMAND("CONDITION_JOINCOMMAND", "所有收到的命令"),

    CONDITION_OUTCOMMAND("CONDITION_OUTCOMMAND", "所有发出的命令"),

    CONDITION_MYCOMMAND("CONDITION_MYCOMMAND", "所有发出和执行命令"),

    CONDITION_ALLCOMMAND("CONDITION_ALLCOMMAND", "所有命令"),

    CONDITION_COMPLETEDCOMMAND("CONDITION_COMPLETEDCOMMAND", "已结束场景的命令");

;

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    CommandConditionEnums(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static CommandConditionEnums fromType(String typeName) {
	for (CommandConditionEnums type : CommandConditionEnums.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
