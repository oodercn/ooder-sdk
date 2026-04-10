package net.ooder.annotation;


import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.OverflowType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DivFormAnnotation {


    public String iframeAutoLoad() default "";

    public String ajaxAutoLoad() default "";

    public String width() default "";

    public String height() default "";

    public String html() default "";

    public OverflowType overflow() default OverflowType.auto;

    CustomFormMenu[] customMenu() default {};

    CustomFormEvent[] event() default {};

    CustomFormMenu[] bottombarMenu() default {};

    Class[] customService() default {};


}
