package net.ooder.annotation.view;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MFormViewAnnotation {

    String expression() default "";

    String saveUrl() default "";

    String reSetUrl() default "";

    String dataUrl() default "";

    String searchUrl() default "";

    String caption() default "";

    boolean autoSave() default false;


    ;


}
