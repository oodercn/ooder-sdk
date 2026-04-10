package net.ooder.annotation.event;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomMsgAction;
import net.ooder.annotation.action.CustomFormAction;
import net.ooder.annotation.action.CustomPageAction;

import java.lang.annotation.Annotation;


public enum CustomOnDestroyEventEnum implements ModuleEvent {

    FREE("解除遮罩", new CustomAction[]{CustomMsgAction.FREE}),

    SAVE("保存表单", new CustomAction[]{CustomFormAction.SAVE}),

    RELOADFORM("保存表单", new CustomAction[]{CustomFormAction.SAVE}),

    RELOADPARENT("刷新父级页面", new CustomAction[]{CustomPageAction.RELOADPARENT}),

    CLOSEPARENT("关闭父级页面", new CustomAction[]{CustomPageAction.CLOSEPARENT});

    ModuleEventEnum event;
    CustomAction[] actions;
    String desc;
    @JSONField(name = "return")
    private Boolean _return=true;
    private  String eventReturn;
    CustomOnDestroyEventEnum(String desc, CustomAction[] actions) {
        this.event = ModuleEventEnum.onDestroy;
        this.actions = actions;
        this.desc = desc;
    }

    ;

    CustomOnDestroyEventEnum(ModuleEvent event) {
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


    public ModuleEventEnum getEvent() {
        return event;
    }

    public void setEvent(ModuleEventEnum event) {
        this.event = event;
    }

    public CustomAction[] getActions() {
        return actions;
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }

    @Override
    public boolean _return() {
        return true;
    }
    @Override
    public ModuleEventEnum event() {
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
    public String eventReturn() {
        return eventReturn;
    }

    public String getEventReturn() {
        return eventReturn;
    }

    public void setEventReturn(String eventReturn) {
        this.eventReturn = eventReturn;
    }
    @Override
    public Class<? extends Annotation> annotationType() {
        return ModuleEvent.class;
    }
}
