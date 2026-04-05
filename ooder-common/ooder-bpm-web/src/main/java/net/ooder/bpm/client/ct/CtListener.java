package net.ooder.bpm.client.ct;

import net.ooder.bpm.client.Listener;
import net.ooder.bpm.enums.event.ListenerEnums;
import net.ooder.bpm.enums.event.ListenerTypeEnums;
import net.ooder.annotation.EventEnums;

public class CtListener implements Listener {

    private String listenerId;

    private String listenerName;

    private ListenerEnums listenerEvent;

    private String realizeClass;

    private EventEnums expressionEventType;

    private ListenerTypeEnums expressionListenerType;

    private String expressionStr;

    CtListener(Listener listener){
        this.listenerId=listener.getListenerId();
        this.listenerName=listener.getListenerName();
        this.listenerEvent=listener.getListenerEvent();
        this.realizeClass=listener.getRealizeClass();
        this.expressionEventType=listener.getExpressionEventType();
        this.expressionListenerType=listener.getExpressionListenerType();
        this.expressionStr=listener.getExpressionStr();

    }

    @Override
    public String getListenerId() {
        return listenerId;
    }

    @Override
    public String getListenerName() {
        return listenerName;
    }

    @Override
    public ListenerEnums getListenerEvent() {
        return listenerEvent;
    }

    @Override
    public String getRealizeClass() {
        return realizeClass;
    }

    @Override
    public EventEnums getExpressionEventType() {
        return expressionEventType;
    }

    @Override
    public ListenerTypeEnums getExpressionListenerType() {
        return expressionListenerType;
    }

    @Override
    public String getExpressionStr() {
        return expressionStr;
    }
}
