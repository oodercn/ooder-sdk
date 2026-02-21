package net.ooder.common.org.event;

import net.ooder.annotation.EventTypeEnums;
import net.ooder.common.JDSEvent;

public enum OrgEventTypeEnums  implements EventTypeEnums {
    PersonEvent("PersonEvent", PersonEvent.class),

    OrgEvent("OrgEvent", OrgEvent.class),

    RoleEvent("RoleEvent", RoleEvent.class);


    private String eventName;

    private Class<? extends JDSEvent> eventClass;

    OrgEventTypeEnums(String eventName, Class<? extends JDSEvent> eventClass) {

        this.eventName = eventName;

        this.eventClass = eventClass;

    }

    public static OrgEventTypeEnums fromName(String eventName) {
        for (OrgEventTypeEnums type : OrgEventTypeEnums.values()) {
            if (type.getEventName().equals(eventName)) {
                return type;
            }
        }
        return null;
    }

    public static OrgEventTypeEnums fromEventClass(Class<? extends JDSEvent> eventClass) {
        for (OrgEventTypeEnums type : OrgEventTypeEnums.values()) {
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
