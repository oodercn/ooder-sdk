package net.ooder.annotation;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface CustomListAnnotation {

    String id() default "";

    Class bindClass() default Void.class;

    Class<? extends Enum> enumClass() default Enum.class;

    @NotNull
    boolean dynLoad() default true;

    String filter() default "";

    String[] enums() default {};

    String itemsExpression() default "";


}
