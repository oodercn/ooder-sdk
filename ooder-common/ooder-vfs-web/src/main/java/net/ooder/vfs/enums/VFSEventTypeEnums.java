package net.ooder.vfs.enums;

import  net.ooder.annotation.EventTypeEnums;
import  net.ooder.common.JDSEvent;
import  net.ooder.vfs.engine.event.*;


public enum VFSEventTypeEnums implements EventTypeEnums {

    FileObjectEvent("FileObjectEvent", FileObjectEvent.class),

    FileEvent("FileEvent", FileEvent.class),

    FolderEvent("FolderEvent", FolderEvent.class),

    FileVersionEvent("FileVersionEvent", FileVersionEvent.class);


    private String eventName;

    private Class<? extends JDSEvent> eventClass;

    VFSEventTypeEnums(String eventName, Class<? extends JDSEvent> eventClass) {

        this.eventName = eventName;

        this.eventClass = eventClass;

    }

    public static VFSEventTypeEnums fromName(String eventName) {
        for (VFSEventTypeEnums type : VFSEventTypeEnums.values()) {
            if (type.getEventName().equals(eventName)) {
                return type;
            }
        }
        return null;
    }

    public static VFSEventTypeEnums fromEventClass(Class<? extends JDSEvent> eventClass) {
        for (VFSEventTypeEnums type : VFSEventTypeEnums.values()) {
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

    public void setEventClass(Class<? extends VFSEvent> eventClass) {
        this.eventClass = eventClass;
    }

}
