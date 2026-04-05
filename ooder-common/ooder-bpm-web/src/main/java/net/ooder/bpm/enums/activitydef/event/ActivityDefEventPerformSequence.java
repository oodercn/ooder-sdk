package net.ooder.bpm.enums.activitydef.event;


import net.ooder.annotation.Enumstype;

public enum ActivityDefEventPerformSequence implements Enumstype {

    FIRST("FIRST", "空闲抢占执行"),

    SEQUENCE("SEQUENCE", "顺序执行（执行排队）"),

    MEANWHILE("MEANWHILE", "并行执行办理"),

    AUTOSIGN("AUTOSIGN", "强行发送命令"),

    DEFAULT("DEFAULT", "默认值");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    ActivityDefEventPerformSequence(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefEventPerformSequence fromType(String typeName) {
	for (ActivityDefEventPerformSequence type : ActivityDefEventPerformSequence.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
