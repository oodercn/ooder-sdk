package net.ooder.annotation.event;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomModuleAction;
import net.ooder.annotation.action.CustomMsgAction;

import java.lang.annotation.Annotation;


public enum CustomBeforInvoke implements APIEvent {


    BUSY(APIEventEnum.beforeInvoke, "增加遮罩", new CustomAction[]{CustomMsgAction.BUSY}),
    MESSAGE(APIEventEnum.beforeInvoke, "开始执行", new CustomAction[]{CustomMsgAction.MESSAGE}),
    PROMPT(APIEventEnum.beforeInvoke, "提示对话框", "{args[0].boxing().confirm}", new CustomAction[]{CustomMsgAction.PROMPT}),
    CONFIRM(APIEventEnum.beforeInvoke, "确认操作", "{args[0].boxing().confirm}", new CustomAction[]{CustomMsgAction.CONFIRM}),
    WARN(APIEventEnum.beforeInvoke, "警告操作", "{args[0].boxing().confirm}", new CustomAction[]{CustomMsgAction.WARN}),
    MSG(APIEventEnum.beforeInvoke, "等候消息", new CustomAction[]{CustomMsgAction.MSG}),
    CHECKVALID(APIEventEnum.beforeInvoke, "检查必填项", new CustomAction[]{CustomModuleAction.CHECKVALID});

    APIEventEnum event;
    CustomAction[] actions;
    String desc;
    @JSONField(name = "return")
    private Boolean _return = true;

    private String eventReturn;

    CustomBeforInvoke(APIEventEnum event, String desc, CustomAction[] actions) {
        this.event = event;
        this.actions = actions;
        this.desc = desc;
    }

    CustomBeforInvoke(APIEventEnum event, String desc, String eventReturn, CustomAction[] actions) {
        this.event = event;
        this.eventReturn = eventReturn;
        this.actions = actions;
        this.desc = desc;
    }

    ;

    CustomBeforInvoke(APIEvent event) {
        this.event = event.event();
        this.actions = event.actions();
        this.desc = event.desc();
        this.eventReturn = event.eventReturn();
    }

    @Override
    public String eventName() {
        return name();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEventReturn() {
        return eventReturn;
    }

    public void setEventReturn(String eventReturn) {
        this.eventReturn = eventReturn;
    }

    @Override
    public String eventReturn() {
        return eventReturn;
    }

    @Override
    public boolean _return() {
        return _return;
    }


    public APIEventEnum getEvent() {
        return event;
    }

    public void setEvent(APIEventEnum event) {
        this.event = event;
    }

    public CustomAction[] getActions() {
        return actions;
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }

    @Override
    public APIEventEnum event() {
        return event;
    }

    @Override
    public CustomAction[] actions() {
        return actions;
    }

    @Override
    public String desc() {
        return desc;
    }


    @Override
    public Class<? extends Annotation> annotationType() {
        return APIEvent.class;
    }
}
