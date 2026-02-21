package net.ooder.annotation.event;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomMQTTAction;
import net.ooder.annotation.action.CustomMsgAction;

import java.lang.annotation.Annotation;


public enum CustomMQTTEventEnum implements MQTTEvent {

    CONNECT(MQTTEventEnum.onMsgArrived, "重新链接", new CustomAction[]{CustomMQTTAction.JMQCONFIG, CustomMQTTAction.JMQCONNECT}),
    EXECSCRIPT(MQTTEventEnum.onMsgArrived, "执行脚本", new CustomAction[]{CustomMsgAction.CONFIRM}),
    EXECCMD(MQTTEventEnum.onMsgArrived, "执行命令", new CustomAction[]{CustomMsgAction.CONFIRM});


    MQTTEventEnum event;
    CustomAction[] actions;
    String desc;
    @JSONField(name = "return")
    private Boolean _return=true;
    private  String eventReturn;
    CustomMQTTEventEnum(MQTTEventEnum event, String desc, CustomAction[] actions) {
        this.event = event;
        this.actions = actions;
        this.desc = desc;
    }

    ;

    CustomMQTTEventEnum(MQTTEvent event) {
        this.event = event.event();
        this.actions = event.actions();
        this.desc = event.desc();
    }
    @Override
    public boolean _return() {
        return true;
    }
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public MQTTEventEnum getEvent() {
        return event;
    }

    public void setEvent(MQTTEventEnum event) {
        this.event = event;
    }

    public CustomAction[] getActions() {
        return actions;
    }

    public void setActions(CustomAction[] actions) {
        this.actions = actions;
    }


    @Override
    public String eventName() {
        return name();
    }

    @Override
    public MQTTEventEnum event() {
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
    public String expression() {
        return null;
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
        return MQTTEvent.class;
    }
}
