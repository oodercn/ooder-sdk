package net.ooder.annotation.event;



import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.action.CustomTreeGridAction;


import java.util.ArrayList;
import java.util.List;

public enum CustomContentBlockEvent implements CustomEvent, Enumstype {

    ONTITLECLICK(ContentBlockEventEnum.onTitleClick, "点击标题", "true", new CustomAction[]{CustomPageAction.EDITOR}),

    ONMORECLICK(ContentBlockEventEnum.onMoreClick, "点击更多", "true", new CustomAction[]{CustomPageAction.MORE}),

    SELECTED(ContentBlockEventEnum.onItemSelected, "选中", "true", new CustomAction[]{CustomPageAction.EDITOR}),

    LOADMENU(ContentBlockEventEnum.onContextmenu, "右键菜单", "true", new CustomAction[]{CustomTreeGridAction.LOADMENU});

    private ContentBlockEventEnum eventEnum;

    private String name;

    private String expression;

    public CustomAction[] actions;


    CustomContentBlockEvent(ContentBlockEventEnum eventEnum, String name, String expression, CustomAction[] actions) {
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

    public ContentBlockEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(ContentBlockEventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public CustomAction[] getActions(boolean expar) {
        List<CustomAction> actionTypes = new ArrayList<CustomAction>();
        for (CustomAction actionType : this.actions) {
           // if (EsbUtil.parExpression(getExpression(), Boolean.class) || expar) {
                actionTypes.add(actionType);
           // }
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
