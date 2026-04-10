package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface RepositoryAnnotation {
    String imageClass() default "";

    Class entityClass() default Void.class;

    Class sourceClass() default Void.class;

}
