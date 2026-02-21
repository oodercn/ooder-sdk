package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.OverflowType;
import net.ooder.annotation.ui.PosType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AfterLayoutAnnotation {
    @NotNull
    String id() default "after";

    int max() default 600;

    int min() default 200;

    int size() default 200;

    boolean folded() default false;

    boolean locked() default false;

    boolean hidden() default false;

    boolean cmd() default true;
    @NotNull
    OverflowType overflow() default OverflowType.auto;

    PosType pos()default PosType.after;

    String expression() default "";

    String panelBgClr() default "";

    String itemClass() default "";

    String url() default "";
    @NotNull
    boolean flexSize() default false;
    @NotNull
    boolean transparent() default true;

    String title() default "";


}
