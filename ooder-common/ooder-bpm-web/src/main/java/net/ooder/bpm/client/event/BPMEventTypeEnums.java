package net.ooder.bpm.client.event;

import net.ooder.annotation.EventTypeEnums;
import net.ooder.common.JDSEvent;
import net.ooder.home.event.HomeEvent;

public enum BPMEventTypeEnums  implements EventTypeEnums {

   BPDEvent("BPDEvent", BPDEvent.class),

    ProcessEvent("ProcessEvent", ProcessEvent.class),

    ActivityEvent("ActivityEvent", ActivityEvent.class),

    RightEvent("RightEvent", RightEvent.class);

   
    private String eventName;

    private Class<? extends JDSEvent> eventClass;

    BPMEventTypeEnums(String eventName, Class<? extends JDSEvent> eventClass) {

	this.eventName = eventName;

	this.eventClass = eventClass;

    }

    public static BPMEventTypeEnums fromName(String eventName) {
	for (BPMEventTypeEnums type : BPMEventTypeEnums.values()) {
	    if (type.getEventName().equals(eventName)) {
		return type;
	    }
	}
	return null;
    }

    public static BPMEventTypeEnums fromEventClass(Class<? extends JDSEvent> eventClass) {
	for (BPMEventTypeEnums type : BPMEventTypeEnums.values()) {
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

    public void setEventClass(Class<? extends HomeEvent> eventClass) {
	this.eventClass = eventClass;
    }

}
