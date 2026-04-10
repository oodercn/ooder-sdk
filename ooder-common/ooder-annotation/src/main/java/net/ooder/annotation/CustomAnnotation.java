package net.ooder.annotation;

import net.ooder.annotation.ui.ComboInputType;
import net.ooder.annotation.ui.FontColorEnum;
import net.ooder.annotation.ui.IconColorEnum;
import net.ooder.annotation.ui.ItemColorEnum;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface CustomAnnotation {

    String id() default "";

    String caption() default "";

    String[] enums() default {};

    Class<? extends Enum> enumClass() default Enum.class;

    @NotNull
    boolean uid() default false;

    ComboInputType inputType() default ComboInputType.none;

    @NotNull
    boolean pid() default false;

    boolean captionField() default false;


    boolean readonly() default false;

    boolean disabled() default false;

    String target() default "";

    @NotNull
    boolean hidden() default false;

    int index() default 0;

    String tips() default "";

    String imageClass() default "";


    IconColorEnum iconColor() default IconColorEnum.NONE;

    ItemColorEnum itemColor() default ItemColorEnum.NONE;

    FontColorEnum fontColor() default FontColorEnum.NONE;


}
