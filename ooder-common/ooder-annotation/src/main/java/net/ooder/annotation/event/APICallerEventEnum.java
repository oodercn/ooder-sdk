package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * APICaller组件事件枚举定义
 * 对应JS文件: src/main/js/APICaller.js
 */
public enum APICallerEventEnum implements EventKey {
    beforeInvoke("beforeInvoke", "开始调用", "profile", "requestId"),
    afterInvoke("afterInvoke", "调用后", "profile", "rspData", "requestId"),
    onData("onData", "数据准备完成", "profile", "rspData", "requestId"),
    onExecuteSuccess("onExecuteSuccess", "执行成功", "profile", "rspData", "requestId"),
    onExecuteError("onExecuteError", "执行失败", "profile", "rspData", "requestId"),
    beforeData("beforeData", "开始准备数据", "profile", "rspData", "requestId"),
    onError("onError", "调用失败", "profile", "rspData", "requestId");

    private String event;
    private String[] params;
    private String name;

    APICallerEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    @Override
    public String getEvent() {
        return event;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return event;
    }
}