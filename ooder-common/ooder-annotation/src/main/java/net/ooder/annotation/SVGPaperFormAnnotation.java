package net.ooder.annotation;


import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.OverflowType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SVGPaperFormAnnotation {
    boolean selectable() default true;

    String iframeAutoLoad() default "";

    String html() default "";

    String width() default "32em";

    String height() default "25em";

    OverflowType overflow() default OverflowType.auto;

    boolean scaleChildren() default false;

    int graphicZIndex() default 0;

    CustomFormMenu[] customMenu() default {};

    CustomFormEvent[] event() default {};

    CustomFormMenu[] bottombarMenu() default {};

    Class[] customService() default {};


}
