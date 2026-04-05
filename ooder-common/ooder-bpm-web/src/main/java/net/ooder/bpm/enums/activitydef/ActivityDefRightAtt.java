package net.ooder.bpm.enums.activitydef;


import net.ooder.annotation.Enumstype;

public enum ActivityDefRightAtt implements Enumstype {

    MOVEPERFORMERTO("MOVEPERFORMERTO", "办理后权限"),

    MOVEREADERTO("MOVEREADERTO", "读者后权限"),

    MOVESPONSORTO("MOVESPONSORTO", "发起人权限"),

    PERFORMERSELECTEDID("PERFORMERSELECTEDID", "所有办理人"),

    INSTEADSIGNSELECTED("INSTEADSIGNSELECTED", "代签人"),

    SURROGATEID("SURROGATEID", "代签人"),

    SURROGATENAME("SURROGATENAME", "名称"),

    CANSURROGATE("CANSURROGATE", "是否允许代办"),

    RIGHTGROUP("RIGHTGROUP", "权限组"),

    SPECIALSENDSCOPE("SPECIALSENDSCOPE", "SPECIALSENDSCOPE"),

    CANINSTEADSIGN("CANINSTEADSIGN", "CANINSTEADSIGN"),

    CANTAKEBACK("CANTAKEBACK", "强制收回"),

    CANRESEND("CANRESEND", "是否允许补发"),

    PERFORMTYPE("PERFORMTYPE", "办理类型"),

    PERFORMSEQUENCE("PERFORMSEQUENCE", "办理顺序"),

    COMMANDSELECTEDID("COMMANDSELECTEDID", "命令执行者"),

    READERSELECTEDID("READERSELECTEDID", "阅办人");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }

    ActivityDefRightAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefRightAtt fromType(String typeName) {
	for (ActivityDefRightAtt type : ActivityDefRightAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return PERFORMERSELECTEDID;
    }

}
