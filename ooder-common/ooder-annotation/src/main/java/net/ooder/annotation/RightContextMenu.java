package net.ooder.annotation;

import net.ooder.annotation.menu.ContextMenu;
import net.ooder.annotation.NotNull;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.METHOD})
public @interface RightContextMenu {

    boolean autoHide() default true;

    boolean hideAfterClick() default true;

    @NotNull
    boolean handler() default true;

    boolean formField() default true;

    @NotNull
    boolean lazy() default true;

    @NotNull
    boolean dynLoad() default false;

    String listKey() default "";

    String parentID() default "";

    @NotNull
    String iconFontSize() default "1em";

    String id() default "";

    String itemClass() default "";

    String itemStyle() default "";

    Class[] menuClass() default Void.class;

    ContextMenu[] contextMenu() default {};

    Class serviceClass() default Void.class;

}
