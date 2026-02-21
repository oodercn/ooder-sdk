package net.ooder.annotation.event;


import net.ooder.annotation.CustomAction;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface DatePickerEvent {

    DatePickerEventEnum eventEnum();

    String name()default "";

    String expression() default "";

    CustomAction[] actions();

}
