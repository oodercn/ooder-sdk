package net.ooder.bpm.enums.activityinsthistory;


import net.ooder.annotation.Enumstype;

public enum ActivityInstHistoryAtt implements Enumstype {

    PERFORMER("PERFORMER", "当前办理人"),

    SPONSOR("SPONSOR", "发起人 "),

    READER("READER", "读者组"),

    HISTORYPERFORMER("HISTORYPERFORMER", "曾经办理人"),

    HISSPONSOR("HISSPONSOR", "发送人"),

    HISTORYREADER("HISTORYREADER", "历史读者"),

    NORIGHT("NORIGHT", "无权限组"),

    HISTORYID("HISTORYID", "HISTORYID"),

    RIGHTGRP_NULL("", "无权限组"),

  //  RIGHTGRP_TYPE("NORIGHT", "发送人"),

    NORMAL("NORMAL", "正常"),

    DELETE("DELETE", "已删除"),

    CLEAR("CLEAR", " 已清空"),

    STATUS("STATUS", "个人状态");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    ActivityInstHistoryAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityInstHistoryAtt fromType(String typeName) {
	for (ActivityInstHistoryAtt type : ActivityInstHistoryAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return RIGHTGRP_NULL;
    }


}
