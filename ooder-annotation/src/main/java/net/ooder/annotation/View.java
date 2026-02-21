package net.ooder.annotation;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface View {

    String viewInstId() default "default";

    String domainId() default "default";

    String imageClass() default "";

    Class aggClass() default Void.class;

    Class entityClass() default Void.class;

}
