package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.BorderType;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.Dock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass( viewType = CustomViewType.COMPONENT, componentType = ComponentType.RADIOBOX)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface RadioBoxAnnotation {

    String id() default "";

    Dock dock() default Dock.fill;

    boolean checkBox() default false;

    BorderType borderType() default BorderType.none;

    String tagCmds() default "";


}
