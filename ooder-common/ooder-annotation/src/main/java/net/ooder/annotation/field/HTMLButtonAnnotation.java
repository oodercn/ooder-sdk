package net.ooder.annotation.field;

import net.ooder.annotation.CustomClass;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, componentType = ComponentType.HTMLBUTTON)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface HTMLButtonAnnotation {

    String id() default "";

    String nodeName() default "";

    int tabindex() default 1;

    String width() default "auto";

    String height() default "auto";

    boolean shadow() default true;


    String html() default "";

    String caption() default "";

    String iconFontCode() default "";

    String fontColor() default "";

    String fontSize() default "";

    String fontWeight() default "";

    String fontFamily() default "";


}
