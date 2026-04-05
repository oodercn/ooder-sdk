package net.ooder.esd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ViewType {
    String DIC = "DIC";
    String FORM = "FORM";
    String GRID = "GRID";
    String GALLERY = "GALLERY";
    String value() default "";
}
