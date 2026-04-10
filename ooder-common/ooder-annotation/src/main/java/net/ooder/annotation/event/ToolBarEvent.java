package net.ooder.annotation.event;


import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomLoadClassAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface ToolBarEvent {

    ToolBarEventEnum eventEnum();

    CustomAction[] actions() default {};

    CustomLoadClassAction pageAction() default CustomLoadClassAction.none;

    String desc() default "";

    String name() default "";

    String expression() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";

    String className() default "{args[1].euClassName}";

    String targetFrame() default "{args[1].targetFrame}";

    String childName() default "{args[1].childName}";
}

