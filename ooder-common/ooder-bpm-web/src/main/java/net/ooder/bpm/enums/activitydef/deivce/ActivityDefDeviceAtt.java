package net.ooder.bpm.enums.activitydef.deivce;


import net.ooder.annotation.Enumstype;

public enum ActivityDefDeviceAtt implements Enumstype {

    DURATIONUNIT("DurationUnit", "时间单位"),

    ALERTTIME("AlertTime", "预警时间"),

    SPONSOR("SPONSOR", "发起者"),

    READER("READER", " 参与者"),

    ALL("ALL", "ALL"),

    COMMAND("COMMAND", "COMMAND"),

    COMMANDSELECTEDID("COMMANDSELECTEDID", "COMMANDSELECTEDID"),

    COMMANDEXECTYPE("COMMANDEXECTYPE", "执行方式"),

    COMMANDRETRY("COMMANDRETRY", "超時重试方法"),

    COMMANDRETRYTIMES("COMMANDRETRYTIMES", "重试次数"),

    COMMANDSENDTIMEOUT("COMMANDSENDTIMEOUT", "命令超时时间"),

    COMMANDDELAYTIME("COMMANDDELAYTIME", "延时时间"),
    
    INTERVALTIME("INTERVALTIME", "执行间隔"), 
    

    CANOFFLINESEND("CANOFFLINESEND", "离线发送"),

    PERFORMTYPE("PERFORMTYPE", "选择方式"),

    PERFORMSEQUENCE("PERFORMSEQUENCE", "执行方式"),

    SPECIALSENDSCOPE("SPECIALSENDSCOPE", "离线发送范围"),

    CANSPECIALSEND("CANSPECIALSEND", "离线发送"),

    CANTAKEBACK("CANTAKEBACK", "是否可以退回"),

    CANRESEND("CANRESEND", "是否可以重复发送"),

    DEVICESELECTEDID("DEVICESELECTEDID", "选择设备"),

    MOVEPERFORMERTO("MOVEPERFORMERTO", "办理后权限"),

    MOVEREADERTO("MOVEREADERTO", "读者后权限"),

    MOVESPONSORTO("MOVESPONSORTO", "发起人权限");

    private String type;

    private String name;

    public String getType() {
	return type;
    }


    public String getName() {
	return name;
    }


    ActivityDefDeviceAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefDeviceAtt fromType(String typeName) {
	for (ActivityDefDeviceAtt type : ActivityDefDeviceAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
