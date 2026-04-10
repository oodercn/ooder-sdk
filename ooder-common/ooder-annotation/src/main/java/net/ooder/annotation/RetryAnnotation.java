package net.ooder.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface RetryAnnotation {
    int maxAttempts() default 3;
    long initialInterval() default 1000;
    long maxInterval() default 5000;
    double multiplier() default 2.0;
    Class<? extends Throwable>[] retryOn() default {};
    Class<? extends Throwable>[] noRetryOn() default {};
}