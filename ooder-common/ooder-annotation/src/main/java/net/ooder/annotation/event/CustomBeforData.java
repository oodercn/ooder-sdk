package net.ooder.annotation.event;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomMsgAction;
import net.ooder.annotation.action.BPMAPIAction;
import net.ooder.annotation.action.CustomModuleAction;
import net.ooder.annotation.action.CustomFormAction;

import java.lang.annotation.Annotation;


public enum CustomBeforData implements APIEvent {

    BUSY(APIEventEnum.beforeData, "增加遮罩", new CustomAction[]{CustomMsgAction.BUSY}),
    MESSAGE(APIEventEnum.beforeData, "开始执行", new CustomAction[]{CustomMsgAction.MESSAGE}),
    SAVE(APIEventEnum.beforeData, "保存表单", new CustomAction[]{CustomFormAction.SAVE}),

    SAVEONLY(APIEventEnum.beforeData, "仅保存", new CustomAction[]{BPMAPIAction.SAVEONLY}),
    RELOAD(APIEventEnum.beforeData, "重新装载", new CustomAction[]{CustomFormAction.RELOAD}),
    CHECKVALID(APIEventEnum.beforeData, "检查必填项", new CustomAction[]{CustomModuleAction.CHECKVALID});
    APIEventEnum event;
    CustomAction[] actions;
    String desc;
    @JSONField(name = "return")
    private Boolean _return=true;
    private String eventReturn;
    CustomBeforData(APIEventEnum event, String desc, CustomAction[] actions) {
        this.event = event;
        this.actions = actions;
        this.desc = desc;
    }

    ;

    CustomBeforData(APIEvent event) {
        this.event = event.event();
        this.actions = event.actions();
        this.desc = event.desc();
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
    public boolean _return() {
        return true;
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
    public Class<? extends Annotation> annotationType() {
        return APIEvent.class;
    }
}
