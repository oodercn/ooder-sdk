package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ClassItemAnnotation {
    String imageClass() default "";

    String expression() default "";

    String editorPath() default "";

    String title() default "";

    String value() default "";

}
