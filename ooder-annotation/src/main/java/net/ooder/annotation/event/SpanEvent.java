package net.ooder.annotation.event;



import net.ooder.annotation.CustomAction;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface SpanEvent {

    SpanEventEnum eventEnum();

    String name()default "";

    String expression() default "";

    CustomAction[] actions();

    boolean _return() default true;

    String eventReturn() default "{true}";

}
