package net.ooder.annotation.event;



import net.ooder.annotation.CustomAction;
import net.ooder.annotation.action.CustomGalleryAction;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface GalleryEvent {

    GalleryEventEnum eventEnum();

    CustomGalleryAction[] customActions() default {};

    CustomAction[] actions() default {};

    String desc() default "";

    String name() default "";

    String expression() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";


}
