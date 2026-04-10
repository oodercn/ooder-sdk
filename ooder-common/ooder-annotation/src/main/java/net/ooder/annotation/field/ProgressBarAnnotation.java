package net.ooder.annotation.field;


import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.LayoutType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass( viewType = CustomViewType.COMPONENT, componentType = ComponentType.PROGRESSBAR)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ProgressBarAnnotation {

    String id() default "";

    boolean selectable() default true;

    int value() default 800;

    int maxHeight() default 600;

    String width() default "25.0em";

    String height() default "1.5em";

    String captionTpl() default "* %";

    String fillBG() default "* %";

    LayoutType layoutType() default LayoutType.horizontal;


}
