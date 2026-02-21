package net.ooder.annotation;

import net.ooder.annotation.ui.DockFlexType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface DockAnnotation {

    String conDockStretch() default "";

    DockFlexType conDockFlexFill() default DockFlexType.none;

    boolean dockIgnore() default true;

    boolean dockFloat() default true;

    int dockOrder() default 1;

    String dockMinW() default "";

    String dockMinH() default "";

    String dockMaxW() default "";

    String dockMaxH() default "";

    boolean dockIgnoreFlexFill() default true;

    String dockStretch() default "";


}
