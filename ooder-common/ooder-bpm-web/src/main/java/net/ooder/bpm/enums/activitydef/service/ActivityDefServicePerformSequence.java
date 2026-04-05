package net.ooder.bpm.enums.activitydef.service;


import net.ooder.annotation.Enumstype;

public enum ActivityDefServicePerformSequence implements Enumstype {

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

    ActivityDefServicePerformSequence(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefServicePerformSequence fromType(String typeName) {
	for (ActivityDefServicePerformSequence type : ActivityDefServicePerformSequence.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
