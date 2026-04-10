package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Mobile TimePicker事件枚举定义
 * 对应JS中TDesignMobile.TimePicker组件的EventHandlers定义
 */
public enum MobileTimePickerEventEnum implements EventKey {
    change("change", "变更时"),
    confirm("confirm", "确认时"),
    cancel("cancel", "取消时"),
    show("show", "显示时"),
    hide("hide", "隐藏时"),
    hourChange("hourChange", "小时变更", "hour"),
    minuteChange("minuteChange", "分钟变更", "minute"),
    periodChange("periodChange", "时段变更", "period");

    private String event;
    private String[] params;
    private String name;

    MobileTimePickerEventEnum(String event, String name, String... args) {
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