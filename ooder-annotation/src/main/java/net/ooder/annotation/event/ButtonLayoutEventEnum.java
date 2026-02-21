package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * ButtonLayout组件事件枚举定义
 * 对应JS文件: src/main/js/UI/ButtonLayout.js
 */
public enum ButtonLayoutEventEnum implements EventKey {
    onCmd("onCmd", "命令执行", "profile", "item", "cmdkey", "e", "src"),
    onFlagClick("onFlagClick", "标志点击", "profile", "item", "e", "src"),
    touchstart("touchstart", "触摸开始", "profile", "item", "e", "src"),
    touchmove("touchmove", "触摸移动", "profile", "item", "e", "src"),
    touchend("touchend", "触摸结束", "profile", "item", "e", "src"),
    touchcancel("touchcancel", "触摸取消", "profile", "item", "e", "src"),
    swipe("swipe", "滑动", "profile", "item", "e", "src"),
    swipeleft("swipeleft", "向左滑动", "profile", "item", "e", "src"),
    swiperight("swiperight", "向右滑动", "profile", "item", "e", "src"),
    swipeup("swipeup", "向上滑动", "profile", "item", "e", "src"),
    swipedown("swipedown", "向下滑动", "profile", "item", "e", "src"),
    press("press", "按下", "profile", "item", "e", "src"),
    pressup("pressup", "抬起", "profile", "item", "e", "src");

    private String event;
    private String[] params;
    private String name;

    ButtonLayoutEventEnum(String event, String name, String... args) {
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