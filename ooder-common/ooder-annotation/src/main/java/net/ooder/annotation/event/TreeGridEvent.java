package net.ooder.annotation.event;


import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomTreeGridAction;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface TreeGridEvent {

    TreeGridEventEnum eventEnum();

    CustomTreeGridAction[] customActions() default {};

    CustomAction[] actions() default {};

    String desc() default "";

    String name() default "";

    String expression() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";
}
