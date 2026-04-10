package net.ooder.annotation.event;


import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.action.CustomTreeAction;
import net.ooder.annotation.menu.TreeMenu;

import java.util.ArrayList;
import java.util.List;

public enum CustomTreeEvent implements CustomEvent, Enumstype {

    CLICKDELETE(TreeViewEventEnum.onCmd, TreeMenu.DELETE),

    CLICKSAVEROW(TreeViewEventEnum.onCmd, TreeMenu.SAVEROW),

    CLICKRESET(TreeViewEventEnum.onCmd, TreeMenu.RESET),

    CLICKSORTDOWN(TreeViewEventEnum.onCmd, TreeMenu.SORTDOWN),

    CLICKSORTUP(TreeViewEventEnum.onCmd, TreeMenu.SORTUP),

    CLICKLOADCHILD(TreeViewEventEnum.onCmd, TreeMenu.LOADCHILD),

    TREESAVE(TreeViewEventEnum.onDestroy, "保存选择对象", "true", new CustomAction[]{CustomTreeAction.SAVE}),

    RELOADCHILD(TreeViewEventEnum.onGetContent, "装载子节点", "true", new CustomAction[]{CustomTreeAction.RELOADCHILD}),

    LOADMENU(TreeViewEventEnum.onContextmenu, "右键菜单", "true", new CustomAction[]{CustomTreeAction.LOADMENU}),

    TREERELOAD(TreeViewEventEnum.onRender, "装载数据", "true", new CustomAction[]{CustomTreeAction.RELOAD}),

    TREENODECLICK(TreeViewEventEnum.onClick, "点击节点", "true", new CustomAction[]{}),

    TREENODEDBCLICK(TreeViewEventEnum.onDblclick, "双击节点", "true", new CustomAction[]{}),

    TREENODEEDITOR(TreeViewEventEnum.onClick, "编辑", "true", new CustomAction[]{CustomPageAction.EDITOR});


    private TreeViewEventEnum eventEnum;

    private TreeMenu bindMenu;

    private String name;

    private String expression;

    public CustomAction[] actions;


    CustomTreeEvent(TreeViewEventEnum eventEnum, TreeMenu bindMenu) {
        this.eventEnum = eventEnum;
        this.name = bindMenu.getName();
        this.expression = bindMenu.getExpression();
        this.actions = bindMenu.getActions();
        this.bindMenu = bindMenu;
    }


    CustomTreeEvent(TreeViewEventEnum eventEnum, String name, String expression, CustomAction[] actions) {
        this.eventEnum = eventEnum;
        this.name = name;
        this.expression = expression;
        this.actions = actions;

    }


    public CustomAction[] getActions(boolean expar) {
        List<CustomAction> actionTypes = new ArrayList<CustomAction>();
        for (CustomAction actionType : this.actions) {
            actionTypes.add(actionType);
//            if (EsbUtil.parExpression(getExpression(), Boolean.class) || expar) {
////                actionTypes.add(actionType);
////            }
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

    public TreeViewEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(TreeViewEventEnum eventEnum) {
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
