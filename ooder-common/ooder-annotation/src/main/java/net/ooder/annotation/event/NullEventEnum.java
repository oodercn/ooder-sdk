package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum NullEventEnum implements EventKey {

    onMenuBtnClick("onContextmenu", "菜单按钮点击"),
    beforePopMenu("onContextmenu", "弹出菜单前"),
    onShowSubMenu("onContextmenu", "显示子菜单"),
    onMenuSelected("onContextmenu", "菜单选择"),
    onContextmenu("onContextmenu", "上下文菜单");

    private String event;
    private String[] params;
    private String name;

    NullEventEnum(String event, String name) {
        this.event = event;
        this.name = name;
        this.params = new String[0];
    }

    NullEventEnum(String event, String name, String... args) {
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