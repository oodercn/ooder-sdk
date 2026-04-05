package net.ooder.bpm.enums.event;

import net.ooder.annotation.EventEnums;

public enum ActivityEventEnums implements EventEnums {

    INITED("初始化", "activityInited", 2001),

    ROUTING("新活动开始被激活", "activityActiving", 2002),
    
    ACTIVING("---->开始执行路由操作", "activityRouting", 2003),
    
    
    SAVEING("---->开始执行任务", "activityFormSaveing", 2004),
    
    SAVEED("---->执行完毕完毕", "activityFormSaveed", 2005),
  
    

    ROUTED("----->路由操作完成", "activityRouted", 2004),
  
    ACTIVED("<==>活动完成激活活动已经分裂为多个活动实例", "activityInited", 2005),

  

    SPLITING("活动开始执行路由分裂", "activitySpliting", 2008),

    SPLITED("活动已经分裂为多个活动实例", "activitySplited", 2009),

    JOINING("活动开始执行合并操作", "activityJoining", 2010),

    JOINED("活动已经完成合并操作", "activityJoined", 2011),

    OUTFLOWING("活动开始跳转到其他流程上", "activityOutFlowing", 2012),

    OUTFLOWED("活动已经跳转到其他流程上", "activityOutFlowed", 2013),

    OUTFLOWRETURNING("外流活动开始返回", "activityOutFlowReturning", 2014),

    OUTFLOWRETURNED("外流活动完成返回", "activityOutFlowReturned", 2015),

    SUSPENDING("活动开始挂起", "activitySuspending", 2016),

    SUSPENDED("活动已经挂起", "activitySuspended", 2017),

    RESUMING("活动开始恢复", "activityResuming", 2018),

    RESUMED("活动已经恢复", "activityResumed", 2019),

    COMPLETING("活动开始完成", "activityCompleting", 2020),

    COMPLETED("活动已经完成", "activityCompleted", 2021),

    TAKEBACKING("活动开始收回", "activityTakebacking", 2022),

    TAKEBACKED("活动已经收回", "activityTakebacked", 2023),

    DISP("活动开始展示", "activityDisplay", 2024);

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

    ActivityEventEnums(String name, String method, Integer code) {

	this.name = name;
	this.method = method;
	this.code = code;

    }

    @Override
    public String toString() {
	return method.toString();
    }

    public static ActivityEventEnums fromCode(Integer code) {
	for (ActivityEventEnums type : ActivityEventEnums.values()) {
	    if (type.getCode().equals(code)) {
		return type;
	    }
	}
	return null;
    }

    public static ActivityEventEnums fromMethod(String method) {
	for (ActivityEventEnums type : ActivityEventEnums.values()) {
	    if (type.getMethod().equals(method)) {
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
