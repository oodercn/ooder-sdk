package net.ooder.annotation.event;


import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomGlobalAction;
import net.ooder.annotation.action.CustomPageAction;
import net.ooder.annotation.action.CustomTreeGridAction;
import net.ooder.annotation.menu.TreeGridRowMenu;

import java.util.ArrayList;
import java.util.List;

public enum CustomTreeGridEvent implements CustomEvent, Enumstype {


    CLICKSAVEROW(TreeGridEventEnum.onCommand, TreeGridRowMenu.SAVEROW),

    CLICKSAVEALLROW(TreeGridEventEnum.onCommand, TreeGridRowMenu.SAVEALLROW),

    CLICKDELETEROW(TreeGridEventEnum.onCommand, TreeGridRowMenu.DELETE),

    CLICKEDITORROW(TreeGridEventEnum.onCommand, TreeGridRowMenu.EDITOR),

    CLICKSORTUP(TreeGridEventEnum.onCommand, TreeGridRowMenu.SORTUP),

    CLICKSORTDOWN(TreeGridEventEnum.onCommand, TreeGridRowMenu.SORTDOWN),

    ONDROP(TreeGridEventEnum.onDrop, "放下", "true", new CustomAction[]{CustomGlobalAction.SetDirty}),

    ONDROPTEST(TreeGridEventEnum.onDrop, "测试放下", "true", new CustomAction[]{CustomGlobalAction.TestDrop}),

    ONBEGINEDIT(TreeGridEventEnum.onBeginEdit, "开始编辑", "true", new CustomAction[]{CustomTreeGridAction.BEGINEDIT}),

    ONENDEDIT(TreeGridEventEnum.onEndEdit, "结束编辑", "true", new CustomAction[]{CustomTreeGridAction.ENDEDIT}),

    LOADMENU(TreeGridEventEnum.onContextmenu, "右键菜单", "true", new CustomAction[]{CustomTreeGridAction.LOADMENU}),

    EDITOR(TreeGridEventEnum.onDblclickRow, "编辑", "true", new CustomAction[]{CustomPageAction.EDITOR});


    private TreeGridEventEnum eventEnum;

    private String name;

    private String expression;

    public CustomAction[] actions;

    public TreeGridRowMenu bindMenu;

    CustomTreeGridEvent(TreeGridEventEnum eventEnum, TreeGridRowMenu bindMenu) {
        this.eventEnum = eventEnum;
        this.name = bindMenu.getName();
        this.expression = bindMenu.getExpression();
        this.actions = bindMenu.getActions();
        this.bindMenu = bindMenu;
    }

    CustomTreeGridEvent(TreeGridEventEnum eventEnum, String name, String expression, CustomAction[] actions) {
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

    public TreeGridEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(TreeGridEventEnum eventEnum) {
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

    public TreeGridRowMenu getBindMenu() {
        return bindMenu;
    }

    public void setBindMenu(TreeGridRowMenu bindMenu) {
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
