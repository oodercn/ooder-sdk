package net.ooder.annotation;


import net.ooder.annotation.ui.ThemesType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ViewStylesAnnotation {

    String zoom() default "";

    ThemesType theme() default ThemesType.none;

    String backgroundColor() default "";

    String backgroundImage() default "";

    String backgroundRepeat() default "";

    String backgroundPosition() default "";

    String backgroundAttachment() default "";

}
