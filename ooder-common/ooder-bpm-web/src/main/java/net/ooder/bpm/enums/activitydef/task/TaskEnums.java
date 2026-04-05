package net.ooder.bpm.enums.activitydef.task;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.bpm.enums.command.CommandExecType;
import net.ooder.bpm.enums.command.CommandRetry;

public enum TaskEnums implements AttributeName {

    COMMANDRETRY("COMMANDRETRY", "重试方式", CommandRetry.class),

    COMMANDPERFORMTYPE("COMMANDPERFORMTYPE", "执行方式", CommandExecType.class),

    TASKRETRYTIMES("TASKRETRYTIMES", "重试次数", null),

    TASKDELAYTIME("TASKDELAYTIME", "回调等待时间", null);

    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    TaskEnums(String name, String displayName, Class<? extends Enumstype> clazz) {
	this.name = name;
	this.displayName = displayName;
	this.clazz = clazz;

    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Class<? extends Enumstype> getClazz() {
	return clazz;
    }

    public void setClazz(Class<? extends Enumstype> clazz) {
	this.clazz = clazz;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    @Override
    public String getType() {
	return name;
    }

}
