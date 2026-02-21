package net.ooder.annotation;


import net.ooder.annotation.ViewType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ref {

    RefType ref();

    ViewType view() default ViewType.DIC;

    String pk() default "";

    String fk() default "";
}
