package net.ooder.annotation.event;

import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomFieldAction;

import java.util.ArrayList;
import java.util.List;

public enum CustomFieldEvent implements CustomEvent<FieldEventEnum>, Enumstype {

    ONCLICK(FieldEventEnum.onClick, "编辑", "true", new CustomAction[]{CustomFieldAction.ONCLICK}),

    ONDATACLICK(FieldEventEnum.onDataClick, "编辑", "true", new CustomAction[]{CustomFieldAction.ONDATACLICK}),

    LOADCS(FieldEventEnum.onCommand, "更新样式", "true", new CustomAction[]{CustomFieldAction.LOADCS}),

    UPLOAD(FieldEventEnum.onCommand, "上传文件", "true", new CustomAction[]{CustomFieldAction.UPLOAD}),

    POPEDITOR(FieldEventEnum.onCommand, "弹出编辑", "true", new CustomAction[]{CustomFieldAction.POPEDITOR}),

    LOADITEMS(FieldEventEnum.onFocus, "装载数据项", "true", new CustomAction[]{CustomFieldAction.LOADITEMS}),

    DYNRELOAD(FieldEventEnum.onAutoexpand, "动态刷新", "true", new CustomAction[]{CustomFieldAction.DYNRELOAD}),

    LOADMENU(FieldEventEnum.onContextmenu, "右键菜单", "true", new CustomAction[]{CustomFieldAction.LOADMENU}),

    ONCHANGE(FieldEventEnum.onChange, "编辑", "true", new CustomAction[]{CustomFieldAction.ONCHANGE});


    private FieldEventEnum eventEnum;

    private String name;

    private String expression;

    public CustomAction[] actions;


    CustomFieldEvent(FieldEventEnum eventEnum, String name, String expression, CustomAction[] actions) {
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

    public FieldEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(FieldEventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public CustomAction[] getActions(boolean expar) {
        List<CustomAction> actionTypes = new ArrayList<CustomAction>();
        for (CustomAction actionType : this.actions) {
          //  if (EsbUtil.parExpression(getExpression(), Boolean.class) || expar) {
                actionTypes.add(actionType);
          //  }
        }
        return actionTypes.toArray(new CustomAction[]{});
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }

    public CustomAction[] getActions() {
        return actions;
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

}
