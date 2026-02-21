package net.ooder.annotation;


import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface DialogBtnAnnotation {


    boolean displayBar() default true;

    boolean minBtn() default true;

    boolean maxBtn() default true;

    boolean refreshBtn() default true;

    boolean infoBtn() default false;

    boolean pinBtn() default false;

    boolean landBtn() default false;

    boolean closeBtn() default true;


}
