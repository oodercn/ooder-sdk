package net.ooder.annotation;


import net.ooder.annotation.ui.OverflowType;
import net.ooder.annotation.ui.PosType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MainLayoutAnnotation {
    @NotNull
    String id() default "main";

    boolean folded() default false;

    boolean locked() default false;

    boolean hidden() default false;

    boolean cmd() default true;

    @NotNull
    OverflowType overflow() default OverflowType.auto;

    PosType pos() default PosType.main;

    String expression() default "";

    String panelBgClr() default "";

    String itemClass() default "";

    String url() default "";

    @NotNull
    boolean flexSize() default true;

    @NotNull
    boolean transparent() default true;

    String title() default "";


}
