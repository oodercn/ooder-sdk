package net.ooder.agent.client.home.event;

import  net.ooder.common.JDSEvent;

public enum EventTypeEnums {

    SensorEvent("SensorEvent", SensorEvent.class),

    GatewayEvent("GatewayEvent", GatewayEvent.class),

    DeviceEvent("DeviceEvent", DeviceEvent.class),

    DeviceEndPointEvent("DeviceEndPointEvent", DeviceEndPointEvent.class),

    ZNodeEvent("ZNodeEvent", ZNodeEvent.class),

    DataEvent("DataEvent", DataEvent.class),

    PlaceEvent("PlaceEvent", PlaceEvent.class),

    CommandEvent("CommandEvent", CommandEvent.class);

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

    public void setEventClass(Class<? extends HomeEvent> eventClass) {
	this.eventClass = eventClass;
    }

}
