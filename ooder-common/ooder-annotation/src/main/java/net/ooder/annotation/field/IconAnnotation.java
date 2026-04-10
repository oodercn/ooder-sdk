package net.ooder.annotation.field;

import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ImagePos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.ICON)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface IconAnnotation {


    String id() default "";

    boolean selectable() default true;

    String html() default "";

    String attributes() default "";

    String renderer() default "";

    boolean defaultFocus() default false;

    int tabindex() default -1;

    String image() default "";

    ImagePos imagePos() default ImagePos.center;

    String imageBgSize() default "";

    String imageClass() default "";

    String iconFontCode() default "";

    String iconFontSize() default "";

    String iconColor() default "";


}
