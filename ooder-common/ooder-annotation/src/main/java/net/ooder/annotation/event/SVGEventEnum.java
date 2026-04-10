package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum SVGEventEnum implements EventKey {

    onTextClick("onTextClick", "文本点击", "profile", "e", "src"),
    onClick("onClick", "点击时", "profile", "e", "src"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos"),
    onDblClick("onDblClick", "双击时", "profile", "e", "src");

    private String event;
    private String[] params;
    private String name;

    SVGEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    SVGEventEnum(String event, String name, String... args) {
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