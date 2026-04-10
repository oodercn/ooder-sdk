package net.ooder.annotation.view;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.action.DYNAppendType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DynLoadAnnotation {


    String projectName() default "";

    @NotNull
    String refClassName() default "";

    @NotNull
    String expression() default "";

    String saveUrl() default "";

    String dataUrl() default "";

    @NotNull
    DYNAppendType append() default DYNAppendType.dyn;
}
