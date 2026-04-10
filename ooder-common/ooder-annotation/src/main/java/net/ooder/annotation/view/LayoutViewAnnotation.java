package net.ooder.annotation.view;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface LayoutViewAnnotation {

    String dataUrl() default "";

    boolean cache() default true;

    boolean autoSave() default false;


}
