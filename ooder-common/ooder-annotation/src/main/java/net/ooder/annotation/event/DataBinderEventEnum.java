package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * DataBinder组件事件枚举定义
 * 对应JS文件: src/main/js/DataBinder.js
 */
public enum DataBinderEventEnum implements EventKey {
    beforeInputAlert("beforeInputAlert", "输入提醒前", "profile", "ctrlPrf", "type"),
    beforeUpdateDataToUI("beforeUpdateDataToUI", "更新数据到UI前", "profile", "dataToUI"),
    afterUpdateDataFromUI("afterUpdateDataFromUI", "从UI更新数据后", "profile", "dataFromUI");

    private String event;
    private String[] params;
    private String name;

    DataBinderEventEnum(String event, String name, String... args) {
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