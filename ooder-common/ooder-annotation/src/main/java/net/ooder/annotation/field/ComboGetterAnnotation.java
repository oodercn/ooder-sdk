package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComboInputType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMBOBOX,
        inputType = ComboInputType.getter, componentType = ComponentType.COMBOINPUT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ComboGetterAnnotation {

    String parentID() default "";

    boolean cachePopWnd() default true;

    @NotNull
    String width() default "18.0em";

    @NotNull
    String height() default "5.0em";

    String src() default "";

    boolean dynLoad() default false;

    ComboInputType inputType() default ComboInputType.getter;

    Class bindClass() default Void.class;


}
