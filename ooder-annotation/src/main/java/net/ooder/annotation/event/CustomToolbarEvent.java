package net.ooder.annotation.event;


import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.menu.TreeMenu;

import java.util.ArrayList;
import java.util.List;

public enum CustomToolbarEvent implements CustomEvent, Enumstype {

    CLICKDELETE(ToolBarEventEnum.onClick, TreeMenu.DELETE),

    CLICKSAVEROW(ToolBarEventEnum.onClick, TreeMenu.SAVEROW),

    CLICKRESET(ToolBarEventEnum.onClick, TreeMenu.RESET),

    CLICKSORTDOWN(ToolBarEventEnum.onClick, TreeMenu.SORTDOWN),

    CLICKSORTUP(ToolBarEventEnum.onClick, TreeMenu.SORTUP),

    CLICKLOADCHILD(ToolBarEventEnum.onClick, TreeMenu.LOADCHILD);


    private ToolBarEventEnum eventEnum;

    private TreeMenu bindMenu;

    private String name;

    private String expression;

    public CustomAction[] actions;


    CustomToolbarEvent(ToolBarEventEnum eventEnum, TreeMenu bindMenu) {
        this.eventEnum = eventEnum;
        this.name = bindMenu.getName();
        this.expression = bindMenu.getExpression();
        this.actions = bindMenu.getActions();
        this.bindMenu = bindMenu;
    }


    CustomToolbarEvent(ToolBarEventEnum eventEnum, String name, String expression, CustomAction[] actions) {
        this.eventEnum = eventEnum;
        this.name = name;
        this.expression = expression;
        this.actions = actions;

    }


    public CustomAction[] getActions(boolean expar) {
        List<CustomAction> actionTypes = new ArrayList<CustomAction>();
        for (CustomAction actionType : this.actions) {
            actionTypes.add(actionType);
        }
        return actionTypes.toArray(new CustomAction[]{});
    }


    @Override
    public String getType() {
        return name();
    }

    public String getName() {
        return name;
    }


    @Override
    public ToolBarEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(ToolBarEventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }

    public TreeMenu getBindMenu() {
        return bindMenu;
    }

    public void setBindMenu(TreeMenu bindMenu) {
        this.bindMenu = bindMenu;
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
