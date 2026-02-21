/**
 * $RCSfile: ClusterEventTypeEnums.java,v $
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
package net.ooder.cluster.event;

import net.ooder.common.JDSEvent;

public enum ClusterEventTypeEnums {
    ServerEvent("ServerEvent", net.ooder.cluster.event.ServerEvent.class),

    ServiceEvent("ServiceEvent", net.ooder.cluster.event.ServiceEvent.class);



    private String eventName;

    private Class<? extends JDSEvent> eventClass;

    ClusterEventTypeEnums(String eventName, Class<? extends JDSEvent> eventClass) {

        this.eventName = eventName;

        this.eventClass = eventClass;

    }

    public static ClusterEventTypeEnums fromName(String eventName) {
        for (ClusterEventTypeEnums type : ClusterEventTypeEnums.values()) {
            if (type.getEventName().equals(eventName)) {
                return type;
            }
        }
        return null;
    }

    public static ClusterEventTypeEnums fromEventClass(Class<? extends JDSEvent> eventClass) {
        for (ClusterEventTypeEnums type : ClusterEventTypeEnums.values()) {
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

    public void setEventClass(Class<? extends JDSEvent> eventClass) {
        this.eventClass = eventClass;
    }


    }
