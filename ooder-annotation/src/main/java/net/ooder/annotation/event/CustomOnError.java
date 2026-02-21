package net.ooder.annotation.event;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomMsgAction;
import net.ooder.annotation.action.CustomPageAction;

import java.lang.annotation.Annotation;


public enum CustomOnError implements APIEvent {

    FREE(APIEventEnum.onError, "解除遮罩", new CustomAction[]{CustomMsgAction.FREE}),

    ALERT(APIEventEnum.onError, "调用错误", new CustomAction[]{CustomMsgAction.ALERT}),

    MESSAGE(APIEventEnum.onError, "执行失败", new CustomAction[]{CustomMsgAction.MESSAGE}),

    RELOAD(APIEventEnum.onError, "刷新", new CustomAction[]{CustomPageAction.RELOAD}),

    RELOADPARENT(APIEventEnum.onError, "刷新父级页面", new CustomAction[]{CustomPageAction.RELOADPARENT}),

    CLOSEPARENT(APIEventEnum.onError, "关闭父级页面", new CustomAction[]{CustomPageAction.CLOSEPARENT}),

    CLOSE(APIEventEnum.onError, "关闭", new CustomAction[]{CustomPageAction.CLOSE});

    APIEventEnum event;
    CustomAction[] actions;
    String desc;
    @JSONField(name = "return")
    private Boolean _return=true;
    private String eventReturn;
    CustomOnError(APIEventEnum event, String desc, CustomAction[] actions) {
        this.event = event;
        this.actions = actions;
        this.desc = desc;
    }

    ;

    CustomOnError(APIEvent event) {
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
    public String desc() {
        return desc;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return APIEvent.class;
    }
}
