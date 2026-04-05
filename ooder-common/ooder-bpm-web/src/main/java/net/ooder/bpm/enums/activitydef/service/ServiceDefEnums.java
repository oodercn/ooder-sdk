package net.ooder.bpm.enums.activitydef.service;

import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Enumstype;
import net.ooder.annotation.HttpMethod;
import net.ooder.bpm.enums.activitydef.ActivityDefExecution;
import net.ooder.common.CommonYesNoEnum;

public enum ServiceDefEnums implements AttributeName {

    HttpParams("HttpParams", "请求参数", null),

    Url("Url", "远程地址", null),

    TimeOut("TimeOut", "超时时间（MS）", null),

    TimeOutTrytimes("TimeOutTrytimes", "重试次数(次)", null),

    CanReSend("CanReSend", "是否允许重试", CommonYesNoEnum.class),

    Method("Method", "调用方式", HttpMethod.class),

    RequestType("RequestType", "请求类型", net.ooder.annotation.RequestType.class),
    //
    ResponseType("ResponseType", "返回类型", net.ooder.annotation.ResponseType.class),

    SpecialScope("SpecialScope", "离线发送范围", ActivityDefServiceSpecial.class),

    Execution("Execution", "是否同步调用", ActivityDefExecution.class),

    TimeoutOperation("timeoutOperation", "超时处理办法", ActivityDefServiceDeadLine.class);

    private String name;
    private Class<? extends Enumstype> clazz;
    private String displayName;

    ServiceDefEnums(String name, String displayName, Class<? extends Enumstype> clazz) {

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
