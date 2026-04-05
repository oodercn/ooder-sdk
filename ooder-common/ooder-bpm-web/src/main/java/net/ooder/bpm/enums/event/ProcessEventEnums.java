package net.ooder.bpm.enums.event;

import net.ooder.annotation.EventEnums;

public enum ProcessEventEnums implements EventEnums {

    STARTING("开始启动", "processStarting", 1001),

    STARTED("启动完成", "processStarted", 1002),

    SAVING("开始保存", "processSaving", 1003),

    SAVED("保存完成", "processSaved", 1004),

    SUSPENDING("正在被挂起", "processSuspending", 1005),

    SUSPENDED("已经被挂起", "processSuspended", 1006),

    RESUMING("开始恢复", "processResuming", 1007),

    RESUMED("恢复", "processResumed", 1008),

    ABORTING("开始取消", "processAborting", 1009),

    ABORTED("取消", "processAborted", 1010),

    COMPLETING("开始完成", "processCompleting", 1011),

    COMPLETED("已经完成", "processCompleted", 1012),

    DELETING("正在被删除", "processDeleting", 1013),

    DELETED("已经被删除", "processDeleted", 1014);

    private String name;

    private Integer code;

    private String method;

    public Integer getCode() {
	return code;
    }

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setCode(Integer code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    ProcessEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return code.toString();
    }

    public static ProcessEventEnums fromMethod(String method) {
	for (ProcessEventEnums type : ProcessEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    public static ProcessEventEnums fromCode(Integer code) {
	for (ProcessEventEnums type : ProcessEventEnums.values()) {
	    if (type.getCode().equals(code)) {
		return type;
	    }
	}
	return null;
    }

    public static ActivityEventEnums fromType(String method) {
	for (ActivityEventEnums type : ActivityEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
		return type;
	    }
	}
	return null;
    }

    @Override
    public String getType() {
	return method.toString();
    }

}
