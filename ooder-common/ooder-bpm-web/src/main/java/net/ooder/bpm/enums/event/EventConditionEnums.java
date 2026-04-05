package net.ooder.bpm.enums.event;


import net.ooder.annotation.Enumstype;

public enum EventConditionEnums implements Enumstype {

    CONDITION_WAITEDEVENT("CONDITION_WAITEDEVENT", "等待消费事件"),

    CONDITION_CURRENTEVENT("CONDITION_CURRENTEVENT", "正在消费事件"),

    CONDITION_JOINEVENT("CONDITION_JOINEVENT", "收到事件消息"),

    CONDITION_OUTEVENT("CONDITION_OUTEVENT", "发出事件消息"),

    CONDITION_MYEVENT("CONDITION_MYEVENT", "参与事件"),

    CONDITION_ALLEVENT("CONDITION_ALLEVENT", "所有事件"),

    CONDITION_COMPLETEDEVENT("CONDITION_COMPLETEDEVENT", "已结束场景的工作");

;

    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    EventConditionEnums(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static EventConditionEnums fromType(String typeName) {
	for (EventConditionEnums type : EventConditionEnums.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
