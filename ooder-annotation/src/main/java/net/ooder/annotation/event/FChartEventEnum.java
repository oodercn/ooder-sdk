package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum FChartEventEnum implements EventKey {
    onFusionChartsEvent("onFusionChartsEvent", "FusionCharts事件", "profile", "eventType", "params"),
    onDataClick("onDataClick", "数据点击", "profile", "data"),
    onLabelClick("onLabelClick", "标签点击", "profile", "label"),
    onAnnotationClick("onAnnotationClick", "注解点击", "profile", "annotation"),

    onShowTips("onShowTips", "显示提示", "profile", "node", "pos"),
    onMediaEvent("onMediaEvent", "媒体事件", "profile", "eventType", "params");

    private String event;
    private String[] params;
    private String name;

    FChartEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    FChartEventEnum(String event, String name, String... args) {
        this.event = event;
        this.name = name;
        this.params = args;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String[] getParams() {
        return params;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return event;
    }
}