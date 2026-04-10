package net.ooder.annotation.event;

import net.ooder.common.EventKey;

/**
 * Block组件事件枚举定义
 * 对应JS文件: src/main/js/UI/Block.js
 */
public enum BlockEventEnum implements EventKey {
    onClickPanel("onClickPanel", "点击面板", "profile", "e", "src"),
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
    pressup("pressup", "抬起", "profile", "item", "e", "src"),
    pan("pan", "平移", "profile", "item", "e", "src"),
    panstart("panstart", "平移开始", "profile", "item", "e", "src"),
    panmove("panmove", "平移移动", "profile", "item", "e", "src"),
    panend("panend", "平移结束", "profile", "item", "e", "src"),
    pancancel("pancancel", "平移取消", "profile", "item", "e", "src"),
    panleft("panleft", "向左平移", "profile", "item", "e", "src"),
    panright("panright", "向右平移", "profile", "item", "e", "src"),
    panup("panup", "向上平移", "profile", "item", "e", "src"),
    pandown("pandown", "向下平移", "profile", "item", "e", "src"),
    pinch("pinch", "捏合", "profile", "item", "e", "src"),
    pinchstart("pinchstart", "捏合开始", "profile", "item", "e", "src"),
    pinchmove("pinchmove", "捏合移动", "profile", "item", "e", "src"),
    pinchend("pinchend", "捏合结束", "profile", "item", "e", "src"),
    pinchcancel("pinchcancel", "捏合取消", "profile", "item", "e", "src"),
    pinchin("pinchin", "捏合内缩", "profile", "item", "e", "src"),
    pinchout("pinchout", "捏合外扩", "profile", "item", "e", "src"),
    rotate("rotate", "旋转", "profile", "item", "e", "src"),
    rotatestart("rotatestart", "旋转开始", "profile", "item", "e", "src"),
    rotatemove("rotatemove", "旋转移动", "profile", "item", "e", "src"),
    rotateend("rotateend", "旋转结束", "profile", "item", "e", "src"),
    rotatecancel("rotatecancel", "旋转取消", "profile", "item", "e", "src");

    private String event;
    private String[] params;
    private String name;

    BlockEventEnum(String event, String name, String... args) {
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