package net.ooder.annotation.event;


import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomGalleryAction;
import net.ooder.annotation.action.CustomPageAction;

import java.util.ArrayList;
import java.util.List;

public enum CustomGalleryEvent implements CustomEvent, Enumstype {

    FLAGCLICK(GalleryEventEnum.onFlagClick, "点击下标", "true", new CustomAction[]{CustomGalleryAction.FLAGCLICK}),
    SWIPELEFT(GalleryEventEnum.swipeleft, "左滑上一屏", "true", new CustomAction[]{CustomGalleryAction.LASTPAGE}),
    SWIPERIGHT(GalleryEventEnum.swiperight, "右滑下一屏", "true", new CustomAction[]{CustomGalleryAction.NEXTPAGE}),
//    PANMOVE(GalleryEventEnum.panmove, "移动", "true", new CustomAction[]{CustomGalleryAction.SORT}),
//    PANEND(GalleryEventEnum.panend, "移除", "true", new CustomAction[]{CustomGalleryAction.DELETE, CustomGalleryAction.RELOAD}),
    RELOAD(GalleryEventEnum.onRender, "装载数据", "true", new CustomAction[]{CustomGalleryAction.RELOAD}),
    ONDBLCLICK(GalleryEventEnum.onDblclick, "双击", "true", new CustomAction[]{CustomPageAction.EDITOR}),
    SELECTED(GalleryEventEnum.onItemSelected, "选中", "true", new CustomAction[]{CustomPageAction.EDITOR}),
    LOADMENU(GalleryEventEnum.onContextmenu, "右键菜单", "true", new CustomAction[]{CustomGalleryAction.LOADMENU});
    private GalleryEventEnum eventEnum;

    private String name;

    private String expression;

    public CustomAction[] actions;


    CustomGalleryEvent(GalleryEventEnum eventEnum, String name, String expression, CustomAction[] actions) {
        this.eventEnum = eventEnum;
        this.name = name;
        this.expression = expression;
        this.actions = actions;

    }


    @Override
    public String getType() {
        return name();
    }

    public String getName() {
        return name;
    }

    public GalleryEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(GalleryEventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public CustomAction[] getActions(boolean expar) {
        List<CustomAction> actionTypes = new ArrayList<CustomAction>();
        for (CustomAction actionType : this.actions) {
            actionTypes.add(actionType);
//            if (EsbUtil.parExpression(getExpression(), Boolean.class) || expar) {
//                actionTypes.add(actionType);
//            }
        }

        return actionTypes.toArray(new CustomAction[]{});
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }


    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomAction[] getActions() {
        return actions;
    }
}
