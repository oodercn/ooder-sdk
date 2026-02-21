package net.ooder.annotation;

import net.ooder.annotation.ui.OverflowType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.TYPE})
public @interface DivAnnotation {


    public String iframeAutoLoad() default "";

    public String ajaxAutoLoad() default "";

    public String width() default "";

    public String height() default "";

    public String html() default "";

    public OverflowType overflow() default OverflowType.auto;

}
