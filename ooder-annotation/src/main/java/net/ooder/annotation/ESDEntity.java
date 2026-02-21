package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface ESDEntity {

    Class sourceClass() default Void.class;

    Class serviceClass() default Void.class;

    Class rootClass() default Void.class;

}
