package net.ooder.bpm.enums.activitydef.event;


import net.ooder.annotation.Enumstype;

public enum ActivityDefEventAtt implements Enumstype {

    DURATIONUNIT("DurationUnit", "时间单位"),

    ALERTTIME("AlertTime", "等待时间"),

    DEADLINEOPERATION("DeadLineOperation", "超时处理办法"),

    // CANSPECIALSEND("CanSpecialSend", "是否允许"),

    ATTRIBUTENAME  ("AttributeName", "指定上报类型"),

    DEVICESELECTEDID("DEVICESELECTEDID", "选择设备"),

    DEVICEAPI("DeviceEvent", "网关API监听");

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    
    public String getName() {
	return name;
    }



    ActivityDefEventAtt(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static ActivityDefEventAtt fromType(String typeName) {
	for (ActivityDefEventAtt type : ActivityDefEventAtt.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
