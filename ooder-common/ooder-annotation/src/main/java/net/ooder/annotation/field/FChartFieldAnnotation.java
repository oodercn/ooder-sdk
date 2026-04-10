package net.ooder.annotation.field;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.CustomClass;
import net.ooder.annotation.event.CustomFieldEvent;
import net.ooder.annotation.fchart.FChartType;
import net.ooder.annotation.fchart.RawDataEnums;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.ComponentType;
import net.ooder.annotation.ui.CustomViewType;
import net.ooder.annotation.ui.ModuleViewType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomClass(viewType = CustomViewType.COMPONENT, moduleType = ModuleViewType.CHARTCONFIG, componentType = ComponentType.FCHART)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface FChartFieldAnnotation {


    String renderer() default "";

    boolean selectable() default true;

    String chartCDN() default "";

    @NotNull
    FChartType chartType() default FChartType.Column2D;

    String JSONUrl() default "";

    String XMLUrl() default "";

    CustomFormMenu[] customMenu() default {};

    CustomFieldEvent[] event() default {};

    @NotNull
    Class<? extends Enum> enumClass() default RawDataEnums.class;

    CustomFormMenu[] bottombarMenu() default {};

    Class bindService() default Void.class;

    Class[] customService() default {};


}
