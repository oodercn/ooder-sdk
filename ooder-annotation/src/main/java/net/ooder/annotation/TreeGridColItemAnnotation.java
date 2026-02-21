package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.ui.ComboInputType;
import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface TreeGridColItemAnnotation {
    @NotNull
    boolean flexSize() default false;

    String headerStyle() default "";

    String title() default "";

    Class<? extends Enum> enumClass() default Enum.class;

    ComboInputType inputType() default ComboInputType.input;

    boolean colResizer() default false;

    boolean editable() default false;

    @NotNull
    String width() default "8em";

}
