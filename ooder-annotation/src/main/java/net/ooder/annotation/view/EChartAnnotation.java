package net.ooder.annotation.view;


import net.ooder.annotation.event.CustomFieldEvent;
import net.ooder.annotation.menu.CustomFormMenu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface EChartAnnotation {


    String chartTheme() default "";

    boolean chartResizeSilent() default true;

    String chartCDN() default "";

    String chartCDNGL() default "";

    String bottom() default "";

    String display() default "";

    int chartDevicePixelRatio() default 0;

    String xAxisDateFormatter() default "";


    CustomFormMenu[] customMenu() default {};

    CustomFieldEvent[] event() default {};

    CustomFormMenu[] bottombarMenu() default {};

    Class[] customService() default {};


}
