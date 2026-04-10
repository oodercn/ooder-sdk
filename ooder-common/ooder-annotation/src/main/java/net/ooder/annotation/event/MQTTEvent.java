package net.ooder.annotation.event;



import net.ooder.annotation.CustomAction;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface MQTTEvent {

    MQTTEventEnum event();

    CustomAction[] actions();

    String eventName();

    String desc();

    String expression() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";

}
