package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComboInputType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMBOBOX,
        inputType = {ComboInputType.number, ComboInputType.currency, ComboInputType.counter, ComboInputType.spin},
        componentType = ComponentType.COMBOINPUT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ComboNumberAnnotation {

    @NotNull
    int precision() default 0;
    @NotNull
    String decimalSeparator() default ".";
    @NotNull
    boolean forceFillZero() default true;

    boolean trimTailZero() default false;

    String groupingSeparator() default "";

    @NotNull
    String increment() default "1";

    String min() default "";

    String max() default "";

    String numberTpl() default "";

    @NotNull
    String currencyTpl() default "$ *";


}
