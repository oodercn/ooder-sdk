package net.ooder.annotation.event;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomMsgAction;
import net.ooder.annotation.action.CustomPageAction;

import java.lang.annotation.Annotation;


public enum CustomOnData implements APIEvent {

    RELOAD(APIEventEnum.onData, "刷新", new CustomAction[]{CustomPageAction.RELOAD}),

    FREE(APIEventEnum.onData, "解除遮罩", new CustomAction[]{CustomMsgAction.FREE}),

    MESSAGE(APIEventEnum.onData, "开始获取数据", new CustomAction[]{CustomMsgAction.MESSAGE}),

    RELOADPARENT(APIEventEnum.onData, "刷新父级页面", new CustomAction[]{CustomPageAction.RELOADPARENT}),

    CLOSEPARENT(APIEventEnum.onData, "关闭父级页面", new CustomAction[]{CustomPageAction.CLOSEPARENT}),

    CLOSE(APIEventEnum.onData, "关闭", new CustomAction[]{CustomPageAction.CLOSE});

    APIEventEnum event;
    CustomAction[] actions;
    String desc;
    @JSONField(name = "return")
    private Boolean _return=true;
    private String eventReturn;
    CustomOnData(APIEventEnum event, String desc, CustomAction[] actions) {
        this.event = event;
        this.actions = actions;
        this.desc = desc;
    }

    CustomOnData(APIEvent event) {
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

    @Override
    public boolean _return() {
        return true;
    }

    @Override
    public String eventReturn() {
        return eventReturn;
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
