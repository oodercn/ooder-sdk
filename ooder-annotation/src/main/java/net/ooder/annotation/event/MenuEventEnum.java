package net.ooder.annotation.event;

import net.ooder.common.EventKey;

public enum MenuEventEnum implements EventKey {

    onMenuBtnClick("onMenuBtnClick", "菜单按钮点击", "profile", "item", "src"),
    beforePopMenu("beforePopMenu", "弹出菜单前", "profile", "item", "src"),
    onShowSubMenu("onShowSubMenu", "显示子菜单", "profile", "popProfile", "item", "src"),
    onMenuSelected("onMenuSelected", "菜单选择", "profile", "popProfile", "item", "src"),
    onContextmenu("onContextmenu", "上下文菜单", "profile", "e", "src", "item", "pos");

    private String event;
    private String[] params;
    private String name;

    MenuEventEnum(String event, String name, String... args) {
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