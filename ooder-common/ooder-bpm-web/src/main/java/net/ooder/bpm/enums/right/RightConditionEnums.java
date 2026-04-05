package net.ooder.bpm.enums.right;


import net.ooder.annotation.Enumstype;

public enum RightConditionEnums implements Enumstype {


    CONDITION_WAITEDWORK("CONDITION_WAITEDWORK", "待办工作"),

    CONDITION_CURRENTWORK("CONDITION_CURRENTWORK", "在办工作"),

    CONDITION_CURRENTWORK_NOTSTART("CONDITION_CURRENTWORK_NOTSTART", "草稿"),

    CONDITION_JOINWORK("CONDITION_JOINWORK", "我收到的"),

    CONDITION_OUTWORK("CONDITION_OUTWORK", "我发送的"),

    CONDITION_INWORK("CONDITION_INWORK", "我接收的"),

    CONDITION_DELETEWORK("CONDITION_DELETEWORK", "我删除的"),

    CONDITION_PERFORMWORK("CONDITION_PERFORMWORK", "我办理的"),

    CONDITION_MYWORK("CONDITION_MYWORK", "我的工作（包括了待办和在办工作）"),

    CONDITION_MYWORKNOTREAD("CONDITION_MYWORKNOTREAD", "我的工作（包括了待办和在办工作"),

    CONDITION_COMPLETEDWORK("CONDITION_COMPLETEDWORK", "已办工作"),

    CONDITION_ALLWORK("CONDITION_ALLWORK", "所有工作"),

    CONDITION_PPRCESSCOMPLETEDWORK("CONDITION_PPRCESSCOMPLETEDWORK", "已归档"),

    CONDITION_READ("CONDITION_READ", "阅办工作 "),

    CONDITION_ENDREAD("CONDITION_ENDREAD", "阅闭工作"),









    CONDITION_WAITEDCOMMAND("CONDITION_WAITEDCOMMAND", "等待执行的命令"),

    CONDITION_CURRENTCOMMAND("CONDITION_CURRENTCOMMAND", "正在执行的命令"),

    CONDITION_JOINCOMMAND("CONDITION_JOINCOMMAND", "所有收到的命令"),

    CONDITION_OUTCOMMAND("CONDITION_OUTCOMMAND", "所有发出的命令"),

    CONDITION_MYCOMMAND("CONDITION_MYCOMMAND", "所有发出和执行命令"),

    CONDITION_ALLCOMMAND("CONDITION_ALLCOMMAND", "所有命令"),

    CONDITION_COMPLETEDCOMMAND("CONDITION_COMPLETEDCOMMAND", "已结束场景的命令"),


    CONDITION_WAITEDEVENT("CONDITION_WAITEDEVENT", "等待消费事件"),

    CONDITION_CURRENTEVENT("CONDITION_CURRENTEVENT", "正在消费事件"),

    CONDITION_JOINEVENT("CONDITION_JOINEVENT", "收到事件消息"),

    CONDITION_OUTEVENT("CONDITION_OUTEVENT", "发出事件消息"),

    CONDITION_MYEVENT("CONDITION_MYEVENT", "参与事件"),

    CONDITION_ALLEVENT("CONDITION_ALLEVENT", "所有事件"),

    CONDITION_COMPLETEDEVENT("CONDITION_COMPLETEDEVENT", "已结束场景的工作");


    private String type;

    private String name;

    public String getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    RightConditionEnums(String type, String name) {
	this.type = type;
	this.name = name;

    }

    @Override
    public String toString() {
	return type;
    }

    public static RightConditionEnums fromType(String typeName) {
	for (RightConditionEnums type : RightConditionEnums.values()) {
	    if (type.getType().equals(typeName)) {
		return type;
	    }
	}
	return null;
    }

}
