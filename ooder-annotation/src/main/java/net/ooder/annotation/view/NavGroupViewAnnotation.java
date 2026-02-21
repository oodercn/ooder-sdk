package net.ooder.annotation.view;


import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NavGroupViewAnnotation {
    @NotNull
    boolean autoSave() default false;

    String saveUrl() default "";

    String reSetUrl() default "";

    String dataUrl() default "";

    String searchUrl() default "";

    String caption() default "";

    String expression() default "";

    String editorPath() default "";


}
