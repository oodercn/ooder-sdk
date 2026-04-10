package net.ooder.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FParams {

    String parameterCode() default "";

    String parameterName() default "";

    FormulaParams type();

    String value() default "";

    boolean single() default false;


}