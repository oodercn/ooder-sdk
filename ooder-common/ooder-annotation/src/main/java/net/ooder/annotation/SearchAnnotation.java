package net.ooder.annotation;

import net.ooder.annotation.JoinOperator;
import net.ooder.annotation.NotNull;
import net.ooder.annotation.Operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface SearchAnnotation {


    @NotNull
    Operator operator() default Operator.LIKE;

    @NotNull
    JoinOperator joinOperator() default JoinOperator.JOIN_AND;

    boolean orderAsc() default false;

    boolean orderDesc() default false;

    Class bindClass() default Void.class;

}
