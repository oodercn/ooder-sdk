package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum APICallerMethod implements Enumstype {
    destroy("destroy", "销毁"),
    setHost("setHost", "设置宿主", "value", "alias"),
    setQueryData("setQueryData", "设置查询数据", "data", "path"),
    invoke("invoke", "调用API", "onSuccess", "onFail", "onStart", "onEnd", "mode", "threadid", "options"),
    initAjax("initAjax", "初始化AJAX", "prf", "requestId"),
    beforeInvoke("beforeInvoke", "调用前处理", "prf", "requestId"),
    beforeData("beforeData", "数据处理前", "prf", "requestId", "queryArgs"),
    onError("onError", "错误处理", "prf", "requestId", "error");

    private final String type;
    private final String name;
    private final String[] parameters;

    APICallerMethod(String type, String name, String... parameters) {
        this.type = type;
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return parameters;
    }
}