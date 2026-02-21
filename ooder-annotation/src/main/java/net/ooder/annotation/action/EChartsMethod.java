package net.ooder.annotation.action;

import net.ooder.annotation.Enumstype;

public enum EChartsMethod implements Enumstype {
    getECharts("getECharts", "获取ECharts实例"),
    optionAdapter("optionAdapter", "选项适配器", "option"),
    echarts_call("echarts_call", "调用ECharts方法", "funName", "params"),
    echarts_dispatchAction("echarts_dispatchAction", "派发事件", "payload"),
    echarts_showLoading("echarts_showLoading", "显示加载动画", "type", "opts"),
    echarts_hideLoading("echarts_hideLoading", "隐藏加载动画"),
    echarts_getOption("echarts_getOption", "获取配置项"),
    echarts_setOption("echarts_setOption", "设置配置项", "option"),
    echarts_getDataURL("echarts_getDataURL", "获取数据URL", "opts"),
    echarts_getConnectedDataURL("echarts_getConnectedDataURL", "获取连接数据URL", "opts"),
    echarts_appendData("echarts_appendData", "追加数据", "opts"),
    echarts_clear("echarts_clear", "清空图表"),
    echarts_isDisposed("echarts_isDisposed", "判断是否已销毁");

    private final String type;
    private final String name;
    private final String[] parameters;

    EChartsMethod(String type, String name, String... parameters) {
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