/**
 * $RCSfile: EventTypeEnums.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg.mqtt.event;

import net.ooder.common.JDSEvent;

public enum EventTypeEnums {


    MQTTCommandEvent("MQTTCommandEvent", MQTTCommandEvent.class),
    P2PEvent("P2PEvent", P2PEvent.class),
    TopicEvent("TopicEvent", TopicEvent.class);

    private String eventName;

    private Class<? extends JDSEvent> eventClass;

    EventTypeEnums(String eventName, Class<? extends JDSEvent> eventClass) {

        this.eventName = eventName;

        this.eventClass = eventClass;

    }

    public static EventTypeEnums fromName(String eventName) {
        for (EventTypeEnums type : EventTypeEnums.values()) {
            if (type.getEventName().equals(eventName)) {
                return type;
            }
        }
        return null;
    }

    public static EventTypeEnums fromEventClass(Class<? extends JDSEvent> eventClass) {
        for (EventTypeEnums type : EventTypeEnums.values()) {
            if (type.getEventClass().equals(eventClass)) {
                return type;
            }
        }
        return null;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Class<? extends JDSEvent> getEventClass() {
        return eventClass;
    }

    public void setEventClass(Class<? extends MQTTEvent> eventClass) {
        this.eventClass = eventClass;
    }

}


