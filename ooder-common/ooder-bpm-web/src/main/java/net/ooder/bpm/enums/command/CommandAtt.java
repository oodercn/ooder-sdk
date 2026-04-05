package net.ooder.bpm.enums.command;


import net.ooder.annotation.Enumstype;

public enum CommandAtt implements Enumstype {

    ATT_COMMANDPERFORMTYPE("COMMANDPERFORMTYPE", "执行方式"),

    ATT_COMMANDRETRY("COMMANDRETRY", "重试方式"),

    ATT_CANOFFLINESEND("CANOFFLINESEND", "是否支持离线发送"),

    ATT_COMMANDDELAYTIME("CANOFFLINESEND", "延时时间"),

    ATT_COMMANDRETRYTIMES("TIMEOUT", "超时时间");
    ;

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    CommandAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static CommandAtt fromType(String typeName) {
	for (CommandAtt type : CommandAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
