package net.ooder.annotation.event;

import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomLoadClassAction;
import net.ooder.annotation.action.CustomTreeAction;
import net.ooder.annotation.action.LocalTreeAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE})
public @interface TreeEvent {

    TreeViewEventEnum eventEnum();

    CustomLoadClassAction pageAction() default CustomLoadClassAction.none;

    CustomTreeAction[] customActions() default {};

    LocalTreeAction[] localActions() default {};

    CustomAction[] actions() default {};

    String desc() default "";

    String name() default "";

    String expression() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";


    String className() default "{args[1].euClassName}";

    String targetFrame() default "{args[1].targetFrame}";

    String childName() default "{args[1].childName}";

}
