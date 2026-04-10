package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.OverflowType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.ELEMENT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ElementAnnotation {


    String nodeName() default "div";

    public String className() default "";

    public String attributes();

    String width() default "16.0em";

    String height() default "30.0em";

    boolean selectable() default true;

    String html() default "";

    OverflowType overflow() default OverflowType.auto;

    int tabindex() default -1;


}
