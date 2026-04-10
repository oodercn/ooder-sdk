package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum AnimBinderEventEnum implements EventKey {
    beforeFrame("beforeFrame", "帧前", "profile"),
    onEnd("onEnd", "结束", "profile"),
    
    // 父类事件
    beforePropertyChanged("beforePropertyChanged", "属性变更前", "profile", "name", "value", "ovalue"),
    afterPropertyChanged("afterPropertyChanged", "属性变更后", "profile", "name", "value", "ovalue"),
    onDestroy("onDestroy", "销毁时", "profile"),
    beforeDestroy("beforeDestroy", "销毁前", "profile"),
    afterDestroy("afterDestroy", "销毁后", "profile");

    private String event;
    private String[] params;
    private String name;

    AnimBinderEventEnum(String event, String name, String... args) {
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