package net.ooder.annotation.event;


import net.ooder.annotation.Enumstype;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomFieldAction;

import java.util.ArrayList;
import java.util.List;

public enum CustomHotKeyEvent implements CustomEvent, Enumstype {

    ENTERKEYDOWN(FieldEventEnum.onHotKeydown, "enter", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    ENTERKEYPRESS(FieldEventEnum.onHotKeypress, "enter", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    ENTERKEYUP(FieldEventEnum.onHotKeypress, "enter", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),


    ESCKEYDOWN(FieldEventEnum.onHotKeydown, "esc", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    ESCKEYPRESS(FieldEventEnum.onHotKeypress, "esc", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    ESCKEYUP(FieldEventEnum.onHotKeypress, "esc", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    PGUPKEYDOWN(FieldEventEnum.onHotKeydown, "pgup", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    PGUPKEYPRESS(FieldEventEnum.onHotKeypress, "pgup", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    PGUPKEYUP(FieldEventEnum.onHotKeypress, "pgup", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    PGDNKEYDOWN(FieldEventEnum.onHotKeydown, "pgdn", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    PGDNKEYPRESS(FieldEventEnum.onHotKeypress, "pgdn", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    PGDNKEYUP(FieldEventEnum.onHotKeypress, "pgdn", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    UPKEYDOWN(FieldEventEnum.onHotKeydown, "up", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    UPKEYPRESS(FieldEventEnum.onHotKeypress, "up", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    UPKEYUP(FieldEventEnum.onHotKeypress, "up", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),


    DOWNKEYDOWN(FieldEventEnum.onHotKeydown, "down", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    DOWNKEYPRESS(FieldEventEnum.onHotKeypress, "down", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    DOWNKEYUP(FieldEventEnum.onHotKeypress, "down", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),


    TABKEYDOWN(FieldEventEnum.onHotKeydown, "tab", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    TABKEYPRESS(FieldEventEnum.onHotKeypress, "tab", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    TABKEYUP(FieldEventEnum.onHotKeypress, "tab", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),


    LEFTKEYDOWN(FieldEventEnum.onHotKeydown, "left", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    LEFTKEYPRESS(FieldEventEnum.onHotKeypress, "left", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    LEFTKEYUP(FieldEventEnum.onHotKeypress, "left", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    RIGHTKEYDOWN(FieldEventEnum.onHotKeydown, "right", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    RIGHTKEYPRESS(FieldEventEnum.onHotKeypress, "right", "true", new CustomAction[]{CustomFieldAction.HOTKEY}),

    RIGHTKEYUP(FieldEventEnum.onHotKeypress, "right", "true", new CustomAction[]{CustomFieldAction.HOTKEY});


    private FieldEventEnum eventEnum;

    private String name;

    private String hotKey;

    private String expression;

    public CustomAction[] actions;


    CustomHotKeyEvent(FieldEventEnum eventEnum, String hotKey, String expression, CustomAction[] actions) {
        this.eventEnum = eventEnum;
        ;
        this.name = hotKey;
        this.hotKey = hotKey;
        this.expression = expression;
        this.actions = actions;

    }

    public String getHotKey() {
        return hotKey;
    }

    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

    @Override
    public String getType() {
        return name();
    }

    public String getName() {
        return name;
    }

    @Override
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
