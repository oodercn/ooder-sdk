package net.ooder.annotation;

import net.ooder.annotation.ui.SymbolType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface CustomCondition {
    String left();

    SymbolType symbol();

    String right();

    String expression();

}
