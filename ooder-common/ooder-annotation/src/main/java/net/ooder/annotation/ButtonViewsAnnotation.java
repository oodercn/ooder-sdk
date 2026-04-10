package net.ooder.annotation;

import net.ooder.annotation.NotNull;
import net.ooder.annotation.event.CustomFormEvent;
import net.ooder.annotation.menu.CustomFormMenu;
import net.ooder.annotation.ui.*;
import net.ooder.annotation.ui.*;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ButtonViewsAnnotation {

    String value() default "";

    @NotNull
    BarLocationType barLocation() default BarLocationType.top;

    HAlignType barHAlign() default HAlignType.left;

    VAlignType barVAlign() default VAlignType.bottom;

    @NotNull
    boolean noFoldBar() default false;

    boolean noHandler() default false;

    @NotNull
    String barSize() default "12em";

    String sideBarSize() default "-1";

    @NotNull
    SideBarStatusType sideBarStatus() default SideBarStatusType.expand;

    Class bindService() default Void.class;

    @NotNull
    boolean autoReload() default false;

    Class[] customService() default {};

    CustomFormMenu[] customMenu() default {};

    CustomFormEvent[] event() default {};

    CustomFormMenu[] bottombarMenu() default {};

    @NotNull
    Class<? extends Enum> enumClass() default Enum.class;

    boolean autoIconColor() default true;

    boolean autoItemColor() default true;

    boolean autoFontColor() default false;


    ComponentType[] bindTypes() default {ComponentType.BUTTONVIEWS, ComponentType.TABS, ComponentType.STACKS, ComponentType.FOLDINGTABS};


}
