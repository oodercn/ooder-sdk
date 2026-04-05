package net.ooder.bpm.enums.activityinst;


import net.ooder.annotation.Enumstype;

public enum ActivityInstRightAtt implements Enumstype {

    PERFORMER("PERFORMER", "当前办理人"),

    SPONSOR("SPONSOR", "发起人 "),

    READER("READER", "读者组"),

    HISTORYPERFORMER("HISTORYPERFORMER", "曾经办理人"),

    HISSPONSOR("HISSPONSOR", "发送人"),

    HISTORYREADER("HISTORYREADER", "历史读者"),

    NORIGHT("NORIGHT", "无权限组"),

    COMMAND("COMMAND", "命令"),

    SERVICE("SERVICE", "远程服务"),

    ALL("ALL", "ALL"),

    RIGHTGRP_NULL("", "无权限组"),;

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    
    public String getName() {
	return name;
    }

    ActivityInstRightAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityInstRightAtt fromType(String typeName) {
	for (ActivityInstRightAtt type : ActivityInstRightAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return ALL;
    }

}
