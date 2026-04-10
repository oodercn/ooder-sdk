package net.ooder.annotation.field;

import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomAnimType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.ANIMBINDER)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface AnimBinder {

    CustomAnimType customAnim() default CustomAnimType.blinkAlert;

    String dataBinder() default "";

    String dataField() default "";

    String name() default "";


}
