package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum ImageEventEnum implements EventKey {

    onClick("onClick", "点击时", "profile", "e", "src"),
    onDblclick("onDblclick", "双击时", "profile", "e", "src"),
    onError("onError", "错误时", "profile"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    beforeLoad("beforeLoad", "加载前", "profile"),
    afterLoad("afterLoad", "加载后", "profile", "path", "width", "height");

    private String event;
    private String[] params;
    private String name;

    ImageEventEnum(String event, String name, String... args) {
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