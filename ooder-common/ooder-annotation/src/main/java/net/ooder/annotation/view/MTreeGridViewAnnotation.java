package net.ooder.annotation.view;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MTreeGridViewAnnotation {


    String expression() default "";

    String dataUrl() default "";

    String editorPath() default "";

    String addPath() default "";

    String sortPath() default "";

    String delPath() default "";

    String saveRowPath() default "";

    String saveAllRowPath() default "";
}
