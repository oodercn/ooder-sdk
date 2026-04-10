package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DynTreeNodeAnnotation {

    String imageClass() default "";

    String expression() default "";

    String editorPath() default "";

    String title() default "";

    String width() default "400";

    String height() default "600";

    String value() default "";

    boolean dio() default false;
}

