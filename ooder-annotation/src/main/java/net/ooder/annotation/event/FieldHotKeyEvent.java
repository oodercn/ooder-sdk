package net.ooder.annotation.event;


import net.ooder.annotation.CustomAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE})
public @interface FieldHotKeyEvent {

    HotKeyEventEnum eventEnum();

    String hotKey() default "";

    String expression() default "";

    CustomAction[] actions();

    String desc() default "";

    String name() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";


}
