package net.ooder.annotation;


import net.ooder.common.JDSEvent;

public interface EventTypeEnums extends Enums {

    public String getEventName();

    public Class<? extends JDSEvent> getEventClass();

    ;

}
