package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ModuleViewType;
import net.ooder.annotation.ui.OverflowType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass( viewType = CustomViewType.COMPONENT, moduleType = ModuleViewType.NAVTABSCONFIG, componentType = ComponentType.TABS)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface TabsFieldAnnotation {
    boolean selectable() default true;

    String iframeAutoLoad() default "";

    String html() default "";

    String width() default "32em";

    String height() default "25em";

    OverflowType overflow() default OverflowType.auto;

    boolean scaleChildren() default false;

    int graphicZIndex() default 0;

}
