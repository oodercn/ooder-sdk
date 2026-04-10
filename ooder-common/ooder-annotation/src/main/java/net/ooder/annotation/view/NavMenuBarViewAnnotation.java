package net.ooder.annotation.view;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NavMenuBarViewAnnotation {

    String expression() default "";


    String dataUrl() default "";


}
