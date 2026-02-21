package net.ooder.annotation;

import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface ListMenu {

    CustomMenu[] menus() default {};
    @NotNull
    String iconFontSize() default "1em";

    String id() default "";

    boolean showCaption() default true;

    boolean lazy() default false;

    @NotNull
    boolean dynLoad() default false;

    Class[] menuClasses() default   {};

    public boolean formField() default true;

    public Class bindService() default Void.class;


}
