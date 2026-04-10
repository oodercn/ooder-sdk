package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.Dock;
import net.ooder.annotation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.TENSOR)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface TensorAnnotation {

    String id() default "";

    @NotNull
    Dock dock() default Dock.fill;

    boolean selectable() default true;

    @NotNull
    String width() default "30.0em";

    @NotNull
    String height() default "30.0em";

    @NotNull
    String src() default "/plugins/dist/index.html";

    Class bindClass() default Void.class;

    boolean prepareFormData() default true;

    String uploadUrl() default "";


}
