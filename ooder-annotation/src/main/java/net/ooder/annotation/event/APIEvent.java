package net.ooder.annotation.event;

import net.ooder.annotation.CustomAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface APIEvent {

    String eventName() default "";

    APIEventEnum event();

    CustomAction[] actions();

    String desc() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";
}
