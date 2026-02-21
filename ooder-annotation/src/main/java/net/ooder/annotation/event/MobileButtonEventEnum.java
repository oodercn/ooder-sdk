package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * 移动端Button组件事件枚举定义
 * 对应JS文件: src/main/js/mobile/Basic/Button.js
 */
public enum MobileButtonEventEnum implements EventKey {
    // 点击事件处理
    onClick("onClick", "点击时", "profile", "e", "src", "value"),
    
    onChecked("onChecked", "选中时", "profile", "e", "value"),
    
    // 继承 ood.absValue 的事件处理器
    beforeValueSet("beforeValueSet", "值设置前", "profile", "oldValue", "newValue", "force", "tag"),
    afterValueSet("afterValueSet", "值设置后", "profile", "oldValue", "newValue", "force", "tag"),
    onChange("onChange", "变更时", "profile", "oldValue", "newValue", "force", "tag"),
    
    // 继承 ood.UI 的事件处理器
    beforeRender("beforeRender", "渲染前", "profile"),
    onRender("onRender", "渲染时", "profile"),
    beforeDestroy("beforeDestroy", "销毁前", "profile"),
    afterDestroy("afterDestroy", "销毁后", "profile");

    private String event;
    private String[] params;
    private String name;

    MobileButtonEventEnum(String event, String name, String... args) {
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