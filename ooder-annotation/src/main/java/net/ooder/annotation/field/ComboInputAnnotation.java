package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ComboInputType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(
        viewType = CustomViewType.COMBOBOX,
        inputType = {ComboInputType.input},
        componentType = ComponentType.COMBOINPUT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ComboInputAnnotation {

    String expression() default "";

    String imageBgSize() default "";

    String imageClass() default "";

    String iconFontCode() default "";

    String unit() default "";

    String units() default "";

    String tips() default "";

    String commandBtn() default "";

    String labelCaption() default "";

    @NotNull
    ComboInputType inputType() default ComboInputType.input;

    boolean inputReadonly() default false;


}
